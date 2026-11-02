package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.ability.WeatherEventAbility;
import game.weatherblueprint.Drainable;
import game.weatherterrain.FloodedGround;

import java.util.Set;

/**
 * Represents a Rainstorm Weather Event in the game
 */
public final class RainstormEvent implements WeatherEvent
{
    private static final int SOAKED_TURNS = 3;

    /**
     * Returns the weather event ability that is associated with this weather event
     * @return the rainstorm from the WeatherEventAbility enum
     */
    @Override
    public WeatherEventAbility getType()
    {
        return WeatherEventAbility.RAINSTORM;
    }

    /**
     * Applies the rainstorm weather effect across the specified affected locations on the map.
     * Terrain that is drainable or flood-resistant are not flooded, all other locations are
     * replaced with flooded ground terrain and the weather context class is updated. Actors
     * caught in the flooded ground are marked as Soaked
     * @param map
     * @param affectedLocations
     * @param context
     */
    @Override
    public void apply(GameMap map, Set<Location> affectedLocations, WeatherContext context)
    {
        for (Location location : affectedLocations)
        {
            if (location.getGroundAs(Drainable.class) != null || location.getGround().hasAbility(TerrainAbility.FLOOD_RESISTANT))
            {
                continue;
            }

            Ground originalGround = location.getGround();

            location.setGround(new FloodedGround(originalGround));
            context.applyLatestWeather(location);
            markSoaked(location);
        }
    }

    /**
     * Checks if an actor is present at a specified location and
     * applies a temporary soaked status for a set duration
     * @param location
     */
    private void markSoaked(Location location)
    {
        if (location.containsAnActor())
        {
            Actor actor = location.getActor();
            actor.addStatus(new SoakedStatus(SOAKED_TURNS));
        }
    }
}
