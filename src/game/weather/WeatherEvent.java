package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.WeatherEventAbility;

import java.util.Set;

public interface WeatherEvent
{
    WeatherEventAbility getType();

    void apply(GameMap map, Set<Location> affectedLocations, WeatherContext context);
}
