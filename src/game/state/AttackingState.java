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

    /**
     * Transitions to fleeing below 70% health when a safe escape tile is
     * available and the zombie is not burning. It transitions to healing below
     * 50% health if it is not currently attacking a target, or no valid target
     * is reachable; a poisoned zombie cannot enter the healing state.
     */
    @Override
    public CreatureState nextState(Zombie zombie, GameMap map) {
        if (ZombieSensor.canFlee(zombie, map)) {
            return zombie.getFleeingState();
        }
        boolean hasTargetToAttack = hasAdjacentTarget(zombie, map);
        boolean hasValidTarget = firstStepToTarget(zombie, map) != null;
        if (healthBelow(zombie, 50) && !ZombieSensor.isPoisoned(zombie)
                && (!hasTargetToAttack || !hasValidTarget)) {
            return zombie.getHealingState();
        }
        return this;
    }

    /** Attacks an adjacent target, pursues a reachable target, or wanders if none is reachable. */
    @Override
    public Action getAction(Zombie zombie, GameMap map) {
        Location here = map.locationOf(zombie);
        for (Exit exit : here.getExits()) {
            Location next = exit.getDestination();
            if (isTarget(next)) {
                Actor target = next.getActor();
                return new CreatureAttackAction(target, exit.getName(), zombie.damage(), zombie.hitChance());
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

    /**
     * Temporarily increases the Zombie's attack damage and hit chance. The
     * Zombie restores its base values at the beginning of its next turn.
     */
    @Override
    public void onEnter(Zombie zombie, GameMap map) {
        zombie.boostAttackForCurrentTurn();
    }

    private boolean healthBelow(Zombie zombie, int percentage) {
        return zombie.getStatistic(ActorStatistics.HEALTH) * 100
                < zombie.getMaximumStatistic(ActorStatistics.HEALTH) * percentage;
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

}
