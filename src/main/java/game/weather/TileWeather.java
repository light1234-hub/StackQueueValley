package game.weather;

/**
 * Weather conditions that are assigned to a singular tile based on the weather generator
 */
public final class TileWeather
{
    private final double temperatureC;
    private final double precipitationMmPerHour;
    private final double WindSpeedKmh;
    private final double relativeHumidity;

    /**
     * A constructor that creates an object of the TileWeather class
     * @param temperatureC
     * @param precipitationMmPerHour
     * @param windSpeedKmh
     * @param relativeHumidity
     */
    public TileWeather(double temperatureC, double precipitationMmPerHour,
                       double windSpeedKmh, double relativeHumidity)
    {
        this.temperatureC = temperatureC;
        this.precipitationMmPerHour = precipitationMmPerHour;
        this.WindSpeedKmh = windSpeedKmh;
        this.relativeHumidity = relativeHumidity;
    }

    /**
     * A getter that returns the temperature of the tile
     * @return Temperature of the tile in Degree Celsius
     */
    public double getTemperatureC()
    {
        return temperatureC;
    }

    /**
     * A getter that returns the precipitation of the tile
     * @return precipitation of the tile in millimeters per hour
     */
    public double getPrecipitationMmPerHour()
    {
        return precipitationMmPerHour;
    }

    /**
     * A getter that returns the wind speed of the tile
     * @return wind speed of the tile in kilometers per hour
     */
    public double getWindSpeedKmh()
    {
        return WindSpeedKmh;
    }

    /**
     * A getter that returns the relative humidity of the tile
     * @return relative humidity if the tile in percentage
     */
    public double getRelativeHumidity()
    {
        return relativeHumidity;
    }
}
