package game.weatherapi;

import game.weather.WeatherSnapshot;

/**
 * Retrieves the weather details for a specified geographical location
 */
public interface WeatherDataProvider
{
    /**
     * Retrieves the weather for a certain position (latitude and longitude)
     * @param latitude
     * @param longitude
     * @return the weather details the particular location
     * @throws WeatherDataException if the request fails or the response is invalid
     */
    WeatherSnapshot fetch(double latitude, double longitude)
        throws WeatherDataException;
}
