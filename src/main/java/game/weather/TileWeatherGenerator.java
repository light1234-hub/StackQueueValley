package game.weather;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Generates a singular tiles weather based on the API weather snapshot
 */
public class TileWeatherGenerator
{
    /**
     * Creates an object of the weather in one singular tile
     * @param snapshot
     * @param location
     * @return
     */
    public TileWeather generate(WeatherSnapshot snapshot, Location location)
    {
        double variation = spatialVariation(location.x(),location.y());

        double temperature = snapshot.getTemperatureC() + variation;
        double precipitation = Math.max(0, snapshot.getPrecipitationMM() + variation * 0.1);
        double wind = Math.max(0, snapshot.getWindSpeedKmh() + variation * 2);
        double humidity = clamp(snapshot.getRelativeHumidity() + variation,0,100);

        return new TileWeather(temperature,precipitation,wind,humidity);
    }

    /**
     * Calculates variation based on the tile coordinates
     * @param x
     * @param y
     * @return a small temperature variation
     */
    private double spatialVariation(int x, int y)
    {
        return Math.sin(x * 0.5) * 0.75 + Math.cos(y * 0.5) * 0.75;
    }

    /**
     * Restricts a value to an inclusive range
     * @param value
     * @param min
     * @param max
     * @return a restricted value
     */
    private double clamp(double value, double min, double max)
    {
        return Math.max(min,Math.min(max,value));
    }
}
