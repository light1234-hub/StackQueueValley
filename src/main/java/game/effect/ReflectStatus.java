package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.CreatureControl;
import game.actor.HostileCreature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reflect status (Aegis Elixir). While active the owner takes no damage and the blocked damage
 * is dealt back to the creature that caused it.
 * <p>
 * The engine does not pass the attacker to a statistic, so the attacker is identified from the
 * adjacent hostile creatures: the one whose attack damage equals the blocked amount, and which has
 * not already been reflected onto this turn. If no creature matches (e.g. a modified hit), the
 * next adjacent creature that has not been reflected onto yet takes the damage.
 */
public class ReflectStatus implements Status {
    private final Actor owner;
    private Location lastKnown;
    private int turns;
    private final Set<Actor> reflectedThisTurn = new HashSet<>();

    private ReflectStatus(Actor owner, Location location, int turns) {
        this.owner = owner;
        this.lastKnown = location;
        this.turns = turns;
    }

    /**
     * Applies (or refreshes) the reflect ward on an actor.
     *
     * @param owner actor to protect
     * @param location owner's current location
     * @param turns duration in turns
     */
    public static void applyTo(Actor owner, Location location, int turns) {
        for (ReflectStatus old : owner.statusesOf(ReflectStatus.class)) {
            owner.removeStatus(old);
        }
        ReflectStatus status = new ReflectStatus(owner, location, turns);
        owner.addStatus(status);
        ReflectingHealth health = new ReflectingHealth(
                owner.getMaximumStatistic(ActorStatistics.HEALTH),
                owner.getStatistic(ActorStatistics.HEALTH));
        health.setStatus(status);
        owner.addNewStatistic(ActorStatistics.HEALTH, health);
    }

    /** Sends blocked damage back to the adjacent hostile creature that most likely caused it. */
    void reflect(int blocked) {
        GameMap map = lastKnown.map();
        if (!map.contains(owner)) {
            return;
        }
        List<HostileCreature> adjacent = new ArrayList<>();
        for (Exit exit : map.locationOf(owner).getExits()) {
            Actor other = exit.getDestination().getActor();
            if (other != null) {
                other.asCapability(HostileCreature.class).ifPresent(adjacent::add);
            }
        }
        HostileCreature attacker = null;
        for (HostileCreature c : adjacent) {
            if (!reflectedThisTurn.contains(c) && CreatureControl.attackDamage(c) == blocked) {
                attacker = c;
                break;
            }
        }
        for (HostileCreature c : adjacent) {
            if (attacker == null && !reflectedThisTurn.contains(c)) {
                attacker = c;
            }
        }
        if (attacker == null && !adjacent.isEmpty()) {
            attacker = adjacent.get(0);
        }
        if (attacker == null) {
            return;
        }
        reflectedThisTurn.add(attacker);
        attacker.hurt(blocked);
        if (!attacker.isConscious()) {
            attacker.unconscious(owner, map);
        }
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        lastKnown = location;
        reflectedThisTurn.clear();
        if (turns > 0) {
            turns--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return turns > 0;
    }
}