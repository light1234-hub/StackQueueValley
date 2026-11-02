package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.behaviour.FleeBehaviour;
import game.zombieaction.TwoStepMoveAction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the fleeing state of the zombie
 * When in this state
 */
public class FleeingState implements CreatureState
{
    private final FleeBehaviour fleeBehaviour = new FleeBehaviour();
    private static final int SCAN_RANGE = 3;
    private static final int ADJACENT_RANGE = 1;
    private static final int FOOD_SCAN_RANGE = 2;
    private int sprint_turns = 2;

    /**
     * Determines the next state of the zombie based on the health level
     * and presence of adjacent threats and food scans
     * @param zombie the Zombie whose state is being evaluated
     * @param map the current game map
     * @return the next creature state for the zombie
     */
    @Override
    public CreatureState nextState(Zombie zombie, GameMap map)
    {
        int hpPercent = ZombieSensor.healthPercent(zombie);

        boolean hasAdjThreat = ZombieSensor.hasTargetNearby(zombie,map,ADJACENT_RANGE);
        boolean hasFoodNearby = ZombieSensor.hasFoodNearby(zombie,map,FOOD_SCAN_RANGE);
        boolean hasThreatInScanRange = ZombieSensor.hasTargetNearby(zombie,map,SCAN_RANGE);

        if (hpPercent < ZombieSensor.HEAL_BELOW_PERCENT
                && !ZombieSensor.isPoisoned(zombie))
        {
            return zombie.getHealingState();
        }

        if (!hasThreatInScanRange)
        {
            return zombie.getWanderingState();
        }

        return this;
    }

    /**
     * Computes the action for the zombie in the fleeing states
     * the first 2 turns upon entering the fleeing state allows the zombie to move 2 turns
     * @param zombie the Zombie performing the action
     * @param map the current game map
     * @return the action that the zombie will perform in this turn
     */
    @Override
    public Action getAction(Zombie zombie, GameMap map)
    {
        Location zombielocation = map.locationOf(zombie);

        if (sprint_turns > 0)
        {
            Action twoStep = calculateTwoStep(zombie, zombielocation);

            if (twoStep != null)
            {
                sprint_turns--;
                return twoStep;
            }
        }


        Action fleeAction = fleeBehaviour.operate(zombie, zombielocation);
        return fleeAction != null ? fleeAction : new DoNothingAction();
    }

    /**
     * Performs the one time effect when the zombie enters the fleeing state
     * (pushing back any actors and resetting the sprint_turns back to 2)
     * @param zombie zombie the Zombie entering the state
     * @param map map the current game map
     */
    @Override
    public void onEnter(Zombie zombie, GameMap map)
    {
        sprint_turns = 2;

        Location zombieLoc = map.locationOf(zombie);
        Queue<Location> queue = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();

        queue.add(zombieLoc);
        visited.add(zombieLoc);

        while (!queue.isEmpty())
        {
            Location current = queue.poll();
            int distance = Math.max(Math.abs(current.x() - zombieLoc.x())
                    , Math.abs(current.y() - zombieLoc.y()));

            if (distance > 0 && distance <= 2 && current.containsAnActor())
            {
                Actor victim = current.getActor();
                if (ZombieSensor.isTarget(victim))
                {
                    int pushDx = Integer.compare(current.x(), zombieLoc.x());
                    int pushDy = Integer.compare(current.y(), zombieLoc.y());

                    Location midDestination = ZombieSensor.getRelativeLocation(current,pushDx,pushDy);
                    Location doubleDestination = ZombieSensor.getRelativeLocation(current, pushDx * 2, pushDy * 2);
                    Location pushDestination = null;

                    if (midDestination != null && !midDestination.containsAnActor() && midDestination.canActorEnter(victim)
                    && doubleDestination != null && !doubleDestination.containsAnActor() && doubleDestination.canActorEnter(victim))
                    {
                        pushDestination = doubleDestination;
                    }
                    else if (midDestination != null && !midDestination.containsAnActor() && midDestination.canActorEnter(victim))
                    {
                        pushDestination = midDestination;
                    }

                    if (pushDestination != null)
                    {
                        map.moveActor(victim,pushDestination);
                    }
                }
            }

            if (distance < 2)
            {
                for (Exit exit : current.getExits())
                {
                    Location destination = exit.getDestination();
                    if (!visited.contains(destination))
                    {
                        visited.add(destination);
                        queue.add(destination);
                    }
                }
            }
        }
    }

    /**
     * Calculates a 2-step move action away from the nearest threat location.
     */
    private Action calculateTwoStep(Zombie zombie, Location location)
    {
        // We temporarily utilize FleeBehaviour's threat finding mechanism or replicate it here
        // For simplicity, we scan for threat using the internal helper logic or reuse direction calculations
        // Assuming a threat search is accessible:
        Location threatLocation = ZombieSensor.findNearestTarget(location,SCAN_RANGE);
        if (threatLocation == null)
        {
            return null;
        }

        int dx = threatLocation.x() - location.x();
        int dy = threatLocation.y() - location.y();

        int fleeDx = Integer.compare(0, dx);
        int fleeDy = Integer.compare(0, dy);

        Location twoStepLocation = ZombieSensor.getRelativeLocation(location, fleeDx * 2, fleeDy * 2);
        Location midStepLocation = ZombieSensor.getRelativeLocation(location, fleeDx, fleeDy);

        if (twoStepLocation != null && midStepLocation != null
                && !twoStepLocation.containsAnActor() && twoStepLocation.canActorEnter(zombie)
                && !midStepLocation.containsAnActor() && midStepLocation.canActorEnter(zombie))
        {
            return new TwoStepMoveAction(twoStepLocation, getDirectionString(fleeDx, fleeDy));
        }

        return null;
    }

    /**
     * Converts the coordinate direction into readable string name
     * @param dx
     * @param dy
     * @return
     */
    private String getDirectionString(int dx, int dy)
    {
        int directionKey = (Integer.compare(dx,0) +1) * 3 + (Integer.compare(dy,0) + 1);

        return switch (directionKey)
        {
            case 0 -> "North-West";
            case 1 -> "West";
            case 2 -> "South-West";
            case 3 -> "North";
            case 5 -> "South";
            case 6 -> "North-East";
            case 7 -> "East";
            case 8 -> "South-East";
            default ->  "away";
        };
    }

}
