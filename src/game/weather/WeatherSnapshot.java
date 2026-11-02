package game.weather;

import java.time.Instant;

/**
 * A read only collection of weather measurements from the API
 */
public final class WeatherSnapshot
{
    private final double temperatureC;
    private final double precipitationMM;
    private final double windSpeedKmh;
    private final double relativeHumidity;
    private final Instant observedAt;

    /**
     * A constructor that creates an object of the WeatherSnapshot class
     * @param temperatureC
     * @param precipitationMM
     * @param windSpeedKmh
     * @param relativeHumidity
     * @param observedAt
     */
    public WeatherSnapshot(double temperatureC, double precipitationMM, double windSpeedKmh,
                           double relativeHumidity, Instant observedAt)
    {
        this.temperatureC = temperatureC;
        this.precipitationMM = precipitationMM;
        this.windSpeedKmh = windSpeedKmh;
        this.relativeHumidity = relativeHumidity;
        this.observedAt = observedAt;
    }

    /**
     * A getter that returns the temperature in Degree Celsius
     * @return a double representing the temperature
     */
    public double getTemperatureC()
    {
        return temperatureC;
    }

    /**
     * A getter that returns the precipitation per hour in millimeters
     * @return a double representing the precipitation
     */
    public double getPrecipitationMM()
    {
        return precipitationMM;
    }

    /**
     * A getter that returns the wind speed in kilometers per hour
     * @return a double representing the wind speed
     */
    public double getWindSpeedKmh()
    {
        return windSpeedKmh;
    }

    /**
     * A getter that returns the relative humidity as a percentage
     * @return a double representing the relative humidity
     */
    public double getRelativeHumidity()
    {
        return relativeHumidity;
    }

    /**
     * Returns the exact time the measurement was taken
     * @return an observation time
     */
    public Instant getObservedAt()
    {
        return observedAt;
    }
}
