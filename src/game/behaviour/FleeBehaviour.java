package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.state.ZombieSensor;

/**
 * Behaviour that allows an actor to runaway from another actor, moving 1 tile
 * in one turn in the complete opposite direction of the other actor
 */
public class FleeBehaviour implements Behaviour<Actor, Action>
{
    private static final int SEARCH_RANGE = 5;

    /**
     * Computes the fleeing action depending on the position of the other actor
     * @param entity The entity performing the flee behaviour
     * @param location The location of the current entity
     * @return an action to move away from the threat or null if no threat has been detected
     */
    @Override
    public Action operate(Actor entity, Location location)
    {
        Location threatLocation = ZombieSensor.findNearestTarget(location,SEARCH_RANGE);
        if (threatLocation == null)
        {
            return null;
        }

        int dx = threatLocation.x() - location.x();
        int dy = threatLocation.y() - location.y();

        int fleeDx = Integer.compare(0,dx);
        int fleeDy = Integer.compare(0,dy);

        Location midStepLocation = ZombieSensor.getRelativeLocation(location, fleeDx, fleeDy);

        if (midStepLocation != null && !midStepLocation.containsAnActor()
                && midStepLocation.canActorEnter(entity))
        {
            for (Exit exit: location.getExits())
            {
                if (exit.getDestination().equals(midStepLocation))
                {
                    return exit.getDestination().getMoveAction(entity,exit.getName(),exit.getHotKey());
                }
            }
        }

        Exit bestExit = null;

        double maxDistance = location.x() == threatLocation.x() && location.y() == threatLocation.y()
                ? 0 : Math.hypot(location.x() - threatLocation.x(), location.y() - threatLocation.y());

        for (Exit exit : location.getExits())
        {
            Location destination = exit.getDestination();

            if (!destination.containsAnActor() && destination.canActorEnter(entity))
            {
                double distance = Math.hypot(destination.x() -
                        threatLocation.x(), destination.y() - threatLocation.y());

                if (distance > maxDistance)
                {
                    maxDistance = distance;
                    bestExit = exit;
                }
            }
        }

        if (bestExit != null)
        {
            return bestExit.getDestination().getMoveAction(entity, bestExit.getName(), bestExit.getHotKey());
        }

        return null;
    }


}
