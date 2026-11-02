package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.weatherblueprint.WeatherHazard;

/**
 * A behaviour that allows passive creatures to seek out shelters when there is a weather event taking place
 */
public class SeekShelterBehaviour implements Behaviour<Actor, Action>
{
    private static final int SHELTER_SEARCH_RADIUS = 5;

    /**
     * Determines the actors next location and identifies the move that brings the entity closer to a shelter
     * @param actor The entity performing the behaviour
     * @param location The location of the current entity
     * @return a MoveActorAction in the direction of the shelter or null if there aren't any available shelters
     */
    @Override
    public Action operate(Actor actor, Location location)
    {
        if (!tileAffectedByWeather(location))
        {
            return null;
        }

        Location nearestShelter = null;
        int minDistance = Integer.MAX_VALUE;

        for (Location currentLocation : NearbyLocationFinderBehaviour.find(location,SHELTER_SEARCH_RADIUS))
        {
            if (currentLocation.getGround().hasAbility(TerrainAbility.SHELTER))
            {
                int distance = distanceBetween(location,currentLocation);
                if (distance < minDistance)
                {
                    minDistance = distance;
                    nearestShelter = currentLocation;
                }
            }
        }

        if (nearestShelter != null)
        {
            int currentDistanceToShelter = distanceBetween(location,nearestShelter);

            if (currentDistanceToShelter <= 1)
            {
                return  null;
            }

            MoveActorAction bestMove = null;
            int bestDistance = minDistance;

            for (Exit exit : location.getExits())
            {
                Location destination = exit.getDestination();
                if (destination.canActorEnter(actor))
                {
                    int distanceToShelter = distanceBetween(destination,nearestShelter);

                    if (distanceToShelter < bestDistance && distanceToShelter >= 1)
                    {
                        bestDistance = distanceToShelter;
                        bestMove = new MoveActorAction(destination, exit.getName());
                    }
                }
            }

            if (bestMove != null)
            {
                return bestMove;
            }
        }

        return null;
    }

    /**
     * A helper method that checks if a tile currently has a dangerous hazard
     * @param location
     * @return true if there is a dangerous hazard and false if not
     */
    private boolean tileAffectedByWeather(Location location)
    {
        WeatherHazard hazard = location.getGroundAs(WeatherHazard.class);
        return hazard != null && hazard.isDangerous();
    }

    /**
     * Calculates the distance between 2 points on the map
     * @param x
     * @param y
     * @return  the distance between the two points
     */
    private int distanceBetween(Location x, Location y)
    {
        return  Math.abs(x.x() - y.x()) + Math.abs(x.y() - y.y());
    }



}
