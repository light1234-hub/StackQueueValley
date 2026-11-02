package game.weather;

import game.ability.WeatherEventAbility;

/**
 * Thresholds and contributions requirements for one weather event
 */
public class WeatherConditions
{
    private final WeatherEventAbility eventType;
    private final int minContributingTiles;
    private final Double minRainMmPerHour;
    private final Double minTemperatureC;
    private final Double minWindKmh;
    private final Double minHumidity;

    /**
     * Creates an object of the WeatherConditions class
     * @param eventType
     * @param minContributingTiles
     * @param minRainMmPerHour
     * @param minTemperatureC
     * @param minWindKmh
     * @param minHumidity
     */
    public WeatherConditions(WeatherEventAbility eventType, int minContributingTiles, Double minRainMmPerHour,
                             Double minTemperatureC, Double minWindKmh, Double minHumidity)
    {
        if (minContributingTiles < 1) {
            throw new IllegalArgumentException(
                    "At least one contributing tile is required."
            );
        }

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
     * A getter that returns the smallest number of contributing tiles
     * @return the minimum number of contributing tiles
     */
    public int getMinContributingTiles()
    {
        return minContributingTiles;
    }

    /**
     * Evaluates a whether a given tile contributes towards triggering a weather effect
     * @param weather
     * @return true if at least one weather requirement is fulfilled
     */
    public boolean isContributedBy(TileWeather weather) {
        return meets(weather.getPrecipitationMmPerHour(), minRainMmPerHour)
                || meets(weather.getTemperatureC(), minTemperatureC)
                || meets(weather.getWindSpeedKmh(), minWindKmh)
                || meets(weather.getRelativeHumidity(), minHumidity);
    }

    /**
     * A helper method to check if a specific value meets or exceeds a weather event threshold
     * @param value
     * @param threshold
     * @return true if the value is not null and is greater than or equal to the threshold, false if not
     */
    private boolean meets(double value, Double threshold)
    {
        return threshold != null && value >= threshold;
    }
}
