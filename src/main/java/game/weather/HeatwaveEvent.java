package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.ability.WeatherEventAbility;
import game.weatherterrain.ScorchedGround;

import java.util.Set;


/**
 * Represents a Heatwave Weather Event in the game
 */
public final class HeatwaveEvent implements WeatherEvent
{
    /**
     * Returns the weather event ability that is associated with the Weather Event
     * @return
     */
    @Override
    public WeatherEventAbility getType()
    {
        return WeatherEventAbility.HEATWAVE;
    }

    /**
     * Applies the heatwave weather event across the specified affected locations
     * If the terrain has the puddle ability, it converts the ground into a Scorched ground and
     * updates this in the WeatherContext class.
     * @param map
     * @param affectedLocations
     * @param context
     */
    @Override
    public void apply(GameMap map, Set<Location> affectedLocations, WeatherContext context)
    {
        for (Location location : affectedLocations)
        {
            Ground ground = location.getGround();

            if (ground.hasAbility(TerrainAbility.CAN_BE_PUDDLE))
            {
                location.setGround(new ScorchedGround(ground));
                context.applyLatestWeather(location);
            }
        }
    }
}
