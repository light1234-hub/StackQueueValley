package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.state.ZombieSensor;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

/**
 * Behaviour that allows an actor to runaway from another actor, moving 2 tiles
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
        Location threatLocation = findNearestThreat(location,entity);
        if (threatLocation == null)
        {
            return null;
        }

        int dx = threatLocation.x() - location.x();
        int dy = threatLocation.y() - location.y();

        int fleeDx = Integer.compare(0,dx);
        int fleeDy = Integer.compare(0,dy);

        Location midStepLocation = getRelativeLocation(location, fleeDx, fleeDy);

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

    /**
     * Finds the nearest possible threat within the search range using a breadth first search (BFS)
     * @param start
     * @param actor
     * @return the location of the nearest threat or null if no threat has been detected
     */
    private Location findNearestThreat(Location start, Actor actor)
    {
        Queue<Location> queue = new ArrayDeque<>();
        HashSet<Location> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty())
        {
            Location current = queue.poll();

            if (!current.equals(start) && current.containsAnActor() && ZombieSensor.isTarget(current.getActor()))
            {
                return current;
            }

            int distance = Math.max(Math.abs(current.x() - start.x()), Math.abs(current.y() - start.y()));

            if (distance >= SEARCH_RANGE)
            {
                continue;
            }

            for (Exit exit : current.getExits())
            {
                Location destination = exit.getDestination();
                if (!visited.contains((destination)))
                {
                    visited.add(destination);
                    queue.add(destination);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves the relative location offset from the base location on the map
     * @param base
     * @param deltaX
     * @param deltaY
     * @return the target location or null if the location is out of bounds
     */
    private Location getRelativeLocation(Location base, int deltaX, int deltaY)
    {
        try
        {
            return base.map().at(base.x() + deltaX, base.y() + deltaY);
        } catch (Exception e)
        {
            return null;
        }
    }


}
