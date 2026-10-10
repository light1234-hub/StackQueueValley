package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.behaviour.WanderBehaviour;
import game.effect.ChargeEffect;
import game.effect.FireEffect;
import game.effect.PoisonTrap;
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
        boolean hasTargetToAttack = ZombieSensor.adjacentTargetExit(zombie, map) != null;
        boolean hasValidTarget = firstStepToTarget(zombie, map) != null;
        if (ZombieSensor.healthPercent(zombie) < ZombieSensor.HEAL_BELOW_PERCENT
                && !ZombieSensor.isPoisoned(zombie)
                && (!hasTargetToAttack || !hasValidTarget)) {
            return zombie.getHealingState();
        }
        return this;
    }

    /** Attacks an adjacent target, pursues a reachable target, or wanders if none is reachable. */
    @Override
    public Action getAction(Zombie zombie, GameMap map) {
        Location here = map.locationOf(zombie);

        Exit attackExit = ZombieSensor.adjacentTargetExit(zombie, map);
        if (attackExit != null) {
            Actor target = attackExit.getDestination().getActor();
            return new CreatureAttackAction(
                    target,
                    attackExit.getName(),
                    zombie.damage(),
                    zombie.hitChance()
            );
        }

        // Otherwise, move one step toward the nearest reachable target.
        Exit targetStep = firstStepToTarget(zombie, map);
        if (targetStep != null) {
            return new MoveActorAction(targetStep.getDestination(), targetStep.getName());
        }

        // If no target is reachable, wander.
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

        Location originalLocation = map.locationOf(zombie);
        boolean trapAlreadyPresent = originalLocation.getGround().statusesOf(PoisonTrap.class)
                .stream().anyMatch(PoisonTrap::isStatusActive);
        if (!trapAlreadyPresent) {
            originalLocation.getGround().addStatus(new PoisonTrap());
        }

        Exit step = firstStepToTarget(zombie, map);
        if (step != null) {
            Location destination = step.getDestination();
            boolean onFire = destination.getGround().statusesOf(FireEffect.class)
                    .stream().anyMatch(FireEffect::isStatusActive);
            boolean charged = destination.getGround().statusesOf(ChargeEffect.class)
                    .stream().anyMatch(ChargeEffect::isStatusActive);
            boolean trapped = destination.getGround().statusesOf(PoisonTrap.class)
                    .stream().anyMatch(PoisonTrap::isStatusActive);
            if (!destination.containsAnActor() && destination.canActorEnter(zombie)
                    && !onFire && !charged && !trapped) {
                map.moveActor(zombie, destination);
            }
        }
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
                if (next.containsAnActor()
                        && ZombieSensor.isTarget(next.getActor())) {
                    return current == start ? exit : firstSteps.get(current);
                }
                if (next.containsAnActor() || !next.canActorEnter(zombie)) continue;

                visited.add(next);
                firstSteps.put(next, current == start ? exit : firstSteps.get(current));
                queue.add(next);
            }
        }
        return null;
    }

}
