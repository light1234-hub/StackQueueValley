package game.weather;

import game.ability.WeatherEventAbility;

/**
 * Thresholds and contributions requirements for one weather event
 */
public class WeatherConditions
{
    private final WeatherEventAbility eventType;
    private final int minContributingTiles;
    private final double minRainMmPerHour;
    private final double minTemperatureC;
    private final double minWindKmh;
    private final double minHumidity;

    /**
     * Creates an object of the WeatherConditions class
     * @param eventType
     * @param minContributingTiles
     * @param minRainMmPerHour
     * @param minTemperatureC
     * @param minWindKmh
     * @param minHumidity
     */
    public WeatherConditions(WeatherEventAbility eventType, int minContributingTiles, double minRainMmPerHour,
                             double minTemperatureC, double minWindKmh, double minHumidity)
    {
        this.eventType = eventType;
        this.minContributingTiles = minContributingTiles;
        this.minRainMmPerHour = minRainMmPerHour;
        this.minTemperatureC = minTemperatureC;
        this.minWindKmh = minWindKmh;
        this.minHumidity = minHumidity;
    }

    /**
     * Returns the event produced by this recipe
     * @return event type
     */
    public WeatherEventAbility getEventType()
    {
        return eventType;
    }

    /**
     * Returns the minimum qualifying tile count
     * @return minimum number of contributing tiles
     */
    public int getMinContributingTiles()
    {
        return minContributingTiles;
    }

    /**
     * Returns the precipitation threshold
     * @return minimum precipitation in millimeters per hour
     */
    public double getMinRainMmPerHour()
    {
        return minRainMmPerHour;
    }

    /**
     * Returns the temperature threshold
     * @return minimum temperature in degree Celsius
     */
    public double getMinTemperatureC()
    {
        return minTemperatureC;
    }

    /**
     * Returns the wind threshold
     * @return minimum wind speed in kilometers per hour
     */
    public double getMinWindKmh()
    {
        return minWindKmh;
    }

    /**
     * Returns the humidity threshold
     * @return minimum relative humidity percentage
     */
    public double getMinHumidity()
    {
        return minHumidity;
    }
}
