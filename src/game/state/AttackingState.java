package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.actor.ZombieTarget;
import game.behaviour.WanderBehaviour;
import game.effect.BurnEffect;
import game.effect.ChargeEffect;
import game.effect.FireEffect;
import game.effect.PoisonEffect;
import game.farmeraction.CreatureAttackAction;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * State in which the zombie attacks an adjacent valid target or pursues the
 * nearest reachable target. It can leave this state to flee or heal.
 */
public class AttackingState implements CreatureState {
    private final WanderBehaviour wander = new WanderBehaviour();
    private boolean attackBonusForNextTurn;

    /**
     * Transitions to fleeing below 70% health when a safe escape tile is
     * available and the zombie is not burning. It transitions to healing below
     * 50% health if it is not currently attacking a target, or no valid target
     * is reachable; a poisoned zombie cannot enter the healing state.
     */
    @Override
    public CreatureState nextState(Zombie zombie, GameMap map) {
        if (healthBelow(zombie, 70) && !isBurning(zombie) && hasSafeEscapeTile(zombie, map)) {
            return zombie.getFleeingState();
        }
        boolean hasTargetToAttack = hasAdjacentTarget(zombie, map);
        boolean hasValidTarget = firstStepToTarget(zombie, map) != null;
        if (healthBelow(zombie, 50) && !isPoisoned(zombie)
                && (!hasTargetToAttack || !hasValidTarget)) {
            return zombie.getHealingState();
        }
        return this;
    }

    /** Attacks an adjacent target, pursues a reachable target, or wanders if none is reachable. */
    @Override
    public Action getAction(Zombie zombie, GameMap map) {
        boolean boostedTurn = attackBonusForNextTurn;
        attackBonusForNextTurn = false;

        Location here = map.locationOf(zombie);
        for (Exit exit : here.getExits()) {
            Location next = exit.getDestination();
            if (isTarget(next)) {
                Actor target = next.getActor();
                int damage = zombie.damage();
                int hitChance = zombie.hitChance();
                if (boostedTurn) {
                    damage = (int) Math.round(damage * 1.10);
                    hitChance = Math.min(100, (int) Math.round(hitChance * 1.15));
                }
                return new CreatureAttackAction(target, exit.getName(), damage, hitChance);
            }
        }

        Exit step = firstStepToTarget(zombie, map);
        if (step != null) {
            Location destination = step.getDestination();
            return destination.getMoveAction(zombie, step.getName(), step.getHotKey());
        }

        Action action = wander.operate(zombie, here);
        return action == null ? new DoNothingAction() : action;
    }

    /** Arms a one-turn increase to attack damage and hit chance. */
    @Override
    public void onEnter(Zombie zombie, GameMap map) {
        attackBonusForNextTurn = true;
    }

    private boolean healthBelow(Zombie zombie, int percentage) {
        return zombie.getStatistic(ActorStatistics.HEALTH) * 100
                < zombie.getMaximumStatistic(ActorStatistics.HEALTH) * percentage;
    }

    private boolean isBurning(Zombie zombie) {
        return zombie.statusesOf(BurnEffect.class).stream().anyMatch(BurnEffect::isStatusActive);
    }

    private boolean isPoisoned(Zombie zombie) {
        return zombie.statusesOf(PoisonEffect.class).stream().anyMatch(PoisonEffect::isStatusActive);
    }

    private boolean isTarget(Location location) {
        return location.containsAnActor()
                && location.getActor().hasAbility(ZombieTarget.ZOMBIE_TARGET);
    }

    private boolean hasAdjacentTarget(Zombie zombie, GameMap map) {
        for (Exit exit : map.locationOf(zombie).getExits()) {
            if (isTarget(exit.getDestination())) return true;
        }
        return false;
    }

    /** Finds a shortest first move through free, enterable tiles to a valid target. */
    private Exit firstStepToTarget(Zombie zombie, GameMap map) {
        Location start = map.locationOf(zombie);
        Queue<Location> queue = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();
        Map<Location, Exit> firstSteps = new HashMap<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.remove();
            for (Exit exit : current.getExits()) {
                Location next = exit.getDestination();
                if (visited.contains(next)) continue;
                if (isTarget(next)) return current == start ? exit : firstSteps.get(current);
                if (next.containsAnActor() || !next.canActorEnter(zombie)) continue;

                visited.add(next);
                firstSteps.put(next, current == start ? exit : firstSteps.get(current));
                queue.add(next);
            }
        }
        return null;
    }

    /** Checks for at least one free adjacent tile without active fire or charge. */
    private boolean hasSafeEscapeTile(Zombie zombie, GameMap map) {
        for (Exit exit : map.locationOf(zombie).getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() || !destination.canActorEnter(zombie)) continue;
            boolean onFire = destination.getGround().statusesOf(FireEffect.class)
                    .stream().anyMatch(FireEffect::isStatusActive);
            boolean charged = destination.getGround().statusesOf(ChargeEffect.class)
                    .stream().anyMatch(ChargeEffect::isStatusActive);
            if (!onFire && !charged) return true;
        }
        return false;
    }
}
