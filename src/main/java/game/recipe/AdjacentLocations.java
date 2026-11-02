package game.recipe;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;
import java.util.ArrayList;

public final class AdjacentLocations
{
    private AdjacentLocations()
    {}

    public static List<Location> from(Location location)
    {
        List<Location> adjacent = new ArrayList<>();

        for (Exit exit : location.getExits())
        {
            adjacent.add(exit.getDestination());
        }

        return adjacent;
    }
}
