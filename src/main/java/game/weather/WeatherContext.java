package game.weather;

import edu.monash.fit2099.engine.positions.Location;
import game.weatherterrain.WeatherGround;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the contextual weather data for map locations, tracking the latest recorded
 * tile weather conditions and synchronizing temperature metrics with weather-aware ground terrain
 */
public class WeatherContext
{
    private final Map<Location, TileWeather> latestWeather = new HashMap<>();

    /**
     * Updates the weather data for a collection of tile locations and synchronizes
     * the temperature on any weather ground present at those locations
     * @param tileWeather
     */
    public void updateTileWeather(Map<Location, TileWeather> tileWeather)
    {
        latestWeather.putAll(tileWeather);

        tileWeather.forEach((location,weather) -> {
            WeatherGround ground = location.getGroundAs(WeatherGround.class);

            if (ground != null)
            {
                ground.setTemperature(weather.getTemperatureC());
            }
        });
    }

    /**
     * Applies the latest recorded tile weather conditions to a specific location,
     * it updates the weather ground temperature if both weather data and ground exists
     * @param location
     */
    public void applyLatestWeather(Location location)
    {
        TileWeather weather = latestWeather.get(location);
        WeatherGround ground = location.getGroundAs(WeatherGround.class);

        if (weather != null && ground != null)
        {
            ground.setTemperature(weather.getTemperatureC());
        }
    }

}
