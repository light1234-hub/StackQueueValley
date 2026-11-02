package game.weather;

import edu.monash.fit2099.engine.positions.Location;
import game.weatherterrain.WeatherGround;

import java.util.HashMap;
import java.util.Map;

public class WeatherContext
{
    private final Map<Location, TileWeather> latestWeather = new HashMap<>();

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
