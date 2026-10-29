package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.ability.WeatherEventAbility;
import game.weatherterrain.ScorchedGround;

import java.util.Set;


public final class HeatwaveEvent implements WeatherEvent
{
    @Override
    public WeatherEventAbility getType()
    {
        return WeatherEventAbility.HEATWAVE;
    }

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
