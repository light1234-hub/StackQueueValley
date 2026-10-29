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

    public int getMinContributingTiles()
    {
        return minContributingTiles;
    }

    public boolean isContributedBy(TileWeather weather) {
        return meets(weather.getPrecipitationMmPerHour(), minRainMmPerHour)
                || meets(weather.getTemperatureC(), minTemperatureC)
                || meets(weather.getWindSpeedKmh(), minWindKmh)
                || meets(weather.getRelativeHumidity(), minHumidity);
    }

    private boolean meets(double value, Double threshold)
    {
        return threshold != null && value >= threshold;
    }
}
