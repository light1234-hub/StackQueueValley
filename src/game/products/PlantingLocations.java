package game.products;

import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.terrain.Plant;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for locating plantable locations on the map
 */
public final class PlantingLocations
{
    private static final int ADJACENT_RADIUS = 1;

    /**
     * A private constructor to prevent instantiation
     */
    private PlantingLocations()
    {}


    /**
     * Scans all nearby locations around a origin location to find valid plantable locations
     * @param location
     * @return a list of plantable locations
     */
    public static List<Location> findPlantableLocations (Location location)
    {
        return plantableLocations(location,ADJACENT_RADIUS);
    }

    /**
     * Scans all nearby locations around a certain point within a specified radius
     * to find all plantable locations
     * @param location
     * @return a list of plantable locations
     */
    public static List<Location> plantableLocations(Location location, int radius)
    {
        List<Location> plantableLocations = new ArrayList<>();

        if (location == null)
        {
            return plantableLocations;
        }

        List<Location> nearbyLocations = location.getNearbyLocations(radius);

        for (Location nearby : nearbyLocations)
        {
            boolean isPlantable = nearby.getGround().hasAbility(GroundAbility.FERTILE);
            boolean hasNoCrop = nearby.getGround().getClass() != Plant.class;

            if (isPlantable && hasNoCrop)
            {
                plantableLocations.add(nearby);
            }
        }

        return plantableLocations;
    }
}
