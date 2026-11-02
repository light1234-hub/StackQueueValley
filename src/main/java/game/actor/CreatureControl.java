package game.actor;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.AlchemyAbility;
import game.effect.StunStatus;
import game.effect.TamedStatus;
import game.terrain.TileFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Injects stun / lure-seeking / tamed behaviour into existing HostileCreatures without
 * modifying them. Normal creatures get a priority behaviour; a plain Zombie (which ignores
 * behaviours) is first swapped for a ControlledZombie. Lives in game.actor so it can call the
 * protected addPriorityBehaviour.
 */
public final class CreatureControl {
    private static final Random RNG = new Random();
    private static final int TAMED_DAMAGE = 10;

    private CreatureControl() { }

    public static boolean isStunned(Actor a) {
        return a.statusesOf(StunStatus.class).stream().anyMatch(StunStatus::isStatusActive);
    }

    public static boolean isTamed(Actor a) {
        return a.hasStatus(TamedStatus.class);
    }

    /** Swaps a plain Zombie for a ControlledZombie (same tile, same health); others are returned as is. */
    /** Attack damage of a hostile creature (its damage() method is only visible inside this package). */
    public static int attackDamage(HostileCreature c) {
        return c.damage();
    }

    private static HostileCreature prepare(HostileCreature c, GameMap map) {
        if (c.getClass() != Zombie.class) {
            return c;
        }
        Location at = map.locationOf(c);
        int lost = c.getMaximumStatistic(ActorStatistics.HEALTH) - c.getStatistic(ActorStatistics.HEALTH);
        map.removeActor(c);
        ControlledZombie replacement = new ControlledZombie();
        try {
            map.addActor(replacement, at);
        } catch (GameEngineException e) {
            throw new IllegalStateException(e);
        }
        if (lost > 0) {
            replacement.hurt(lost);
        }
        for (Item carried : new ArrayList<>(c.getInventory().getItems())) {
            c.getInventory().remove(carried);
            replacement.getInventory().add(carried);
        }
        for (Status status : c.statuses()) {
            replacement.addStatus(status);
        }
        for (Enum<?> ability : c.abilities()) {
            replacement.enableAbility(ability);
        }
        return replacement;
    }

    private static void add(HostileCreature c, Behaviour<Actor, Action> behaviour) {
        c.asCapability(BehaviourHost.class).ifPresentOrElse(
                host -> host.addControl(behaviour),
                () -> c.addPriorityBehaviour(behaviour));
    }

    public static void stun(HostileCreature creature, GameMap map, int turns) {
        HostileCreature c = prepare(creature, map);
        c.addStatus(new StunStatus(turns));
        if (!c.hasAbility(AlchemyAbility.STUN_AWARE)) {
            c.enableAbility(AlchemyAbility.STUN_AWARE);
            add(c, new StunBehaviour());
        }
    }

    public static void installLureSeeking(HostileCreature creature, GameMap map) {
        HostileCreature c = prepare(creature, map);
        if (!c.hasAbility(AlchemyAbility.LURE_SEEKING)) {
            c.enableAbility(AlchemyAbility.LURE_SEEKING);
            add(c, new LureSeekBehaviour(c));
        }
    }

    public static void tame(HostileCreature creature, GameMap map) {
        HostileCreature c = prepare(creature, map);
        if (!isTamed(c)) {
            c.addStatus(new TamedStatus());
            add(c, new TamedBehaviour());
        }
    }

    private static Action stepToward(Actor actor, GameMap map, Location target) {
        Location here = map.locationOf(actor);
        Location best = null;
        String bestName = "";
        int bestDist = Integer.MAX_VALUE;
        for (Exit e : here.getExits()) {
            Location d = e.getDestination();
            if (!d.canActorEnter(actor)) {
                continue;
            }
            int dist = Math.max(Math.abs(d.x() - target.x()), Math.abs(d.y() - target.y()));
            if (dist < bestDist) {
                bestDist = dist;
                best = d;
                bestName = e.getName();
            }
        }
        return best == null ? null : new MoveActorAction(best, bestName);
    }

    static class StunBehaviour implements Behaviour<Actor, Action> {
        @Override
        public Action operate(Actor actor, Location location) {
            GameMap map = location.map();
            return isStunned(actor) ? new DoNothingAction() : null;
        }
    }

    static class LureSeekBehaviour implements Behaviour<Actor, Action> {
        private final HostileCreature self;
        private Location target;

        LureSeekBehaviour(HostileCreature self) {
            this.self = self;
        }

        @Override
        public Action operate(Actor actor, Location location) {
            GameMap map = location.map();
            if (isStunned(actor)) {
                return new DoNothingAction();
            }
            if (isTamed(actor)) {
                return null;
            }
            Location here = map.locationOf(actor);
            for (Item item : new ArrayList<>(here.getItems())) {
                if (item.hasAbility(AlchemyAbility.LURE)) {
                    here.removeItem(item);
                    tame(self, map);
                    target = null;
                    return new DoNothingAction();
                }
            }
            if (target == null || !TileFinder.hasLure(target)) {
                List<Location> lures = new ArrayList<>();
                for (Location l : TileFinder.all(map)) {
                    if (TileFinder.hasLure(l)) {
                        lures.add(l);
                    }
                }
                if (lures.isEmpty()) {
                    return null;
                }
                target = lures.get(RNG.nextInt(lures.size()));
            }
            return stepToward(actor, map, target);
        }
    }

    static class TamedBehaviour implements Behaviour<Actor, Action> {
        @Override
        public Action operate(Actor actor, Location location) {
            GameMap map = location.map();
            if (isStunned(actor)) {
                return new DoNothingAction();
            }
            List<Action> moves = new ArrayList<>();
            for (Exit e : map.locationOf(actor).getExits()) {
                Location d = e.getDestination();
                Actor other = d.getActor();
                if (other != null && other.asCapability(HostileCreature.class).isPresent() && !isTamed(other)) {
                    return new TamedAttack(other);
                }
                if (other == null && d.canActorEnter(actor)) {
                    moves.add(new MoveActorAction(d, e.getName()));
                }
            }
            return moves.isEmpty() ? new DoNothingAction() : moves.get(RNG.nextInt(moves.size()));
        }
    }

    /** A tamed creature's attack on an untamed hostile. */
    static class TamedAttack extends Action {
        private final Actor target;

        TamedAttack(Actor target) {
            this.target = target;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            target.hurt(TAMED_DAMAGE);
            String msg = actor + " attacks " + target + " for " + TAMED_DAMAGE + ".";
            if (!target.isConscious()) {
                msg += "\n" + target.unconscious(actor, map);
            }
            return msg;
        }

        @Override
        public String menuDescription(Actor actor) {
            return actor + " attacks " + target;
        }
    }
}