package game.products;

import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Harvestable;
import game.terrain.PlantableGround;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds nearby locations whose ground advertises the {@link PlantableGround}
 * capability and does not currently contain a harvestable crop.
 */
public final class PlantingLocations
{
    /** Default search radius used for merchant crop planting. */
    private static final int ADJACENT_RADIUS = 1;

    /** Prevents instantiation of this utility class. */
    private PlantingLocations()
    {}


    /**
     * Finds eligible locations within the default adjacent radius.
     *
     * @param location origin for the search; {@code null} produces an empty list
     * @return nearby locations with plantable ground and no harvestable crop
     */
    public static List<Location> findPlantableLocations (Location location)
    {
        return plantableLocations(location,ADJACENT_RADIUS);
    }

    /**
     * Finds eligible locations within the requested radius of an origin.
     *
     * @param location origin for the search; {@code null} produces an empty list
     * @param radius search radius accepted by {@link Location#getNearbyLocations(int)}
     * @return nearby locations with plantable ground and no harvestable crop
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
            boolean isPlantable = nearby.getGroundAs(PlantableGround.class) != null;
            boolean hasNoCrop = (nearby.getGroundAs(Harvestable.class) == null);

            if (isPlantable && hasNoCrop)
            {
                plantableLocations.add(nearby);
            }
        }

        return plantableLocations;
    }
}
