package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.WeatherEventAbility;

import java.util.Set;

/**
 * An interface that is implemented by all WeatherEvents
 */
public interface WeatherEvent
{
    /**
     * Returns the WeatherEventAbility that is associated with the WeatherEvent
     * @return
     */
    WeatherEventAbility getType();

    /**
     * Applies the effects of the WeatherEvent to the surrounding tiles
     * @param map
     * @param affectedLocations
     * @param context
     */
    void apply(GameMap map, Set<Location> affectedLocations, WeatherContext context);
}
