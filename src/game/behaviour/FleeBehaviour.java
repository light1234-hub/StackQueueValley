package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.state.ZombieSensor;
import game.zombieaction.TwoStepMoveAction;
import game.actor.ZombieTarget;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

public class FleeBehaviour implements Behaviour<Actor, Action>
{
    private static final int SEARCH_RANGE = 5;

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

        Location twoStepLocation = getRelativeLocation(location, fleeDx * 2, fleeDy * 2);
        Location midStepLocation = getRelativeLocation(location, fleeDx, fleeDy);

        if (twoStepLocation != null && midStepLocation != null
            && !twoStepLocation.containsAnActor() && twoStepLocation.canActorEnter(entity)
            && !midStepLocation.containsAnActor() && midStepLocation.canActorEnter(entity))
        {
            return new TwoStepMoveAction(twoStepLocation,getDirectionString(fleeDx,fleeDy));
        }

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
