package game.weatherapi;

/**
 * A class that represents a failure while requesting
 * or converting the weather data
 */
public class WeatherDataException extends Exception
{
    /**
     * A constructor that creates an exception with a message
     * @param message
     */
    public WeatherDataException(String message)
    {
        super(message);
    }

    /**
     * A constructor that creates an exception with a message and a reason
     * @param message
     * @param reason
     */
    public WeatherDataException(String message, Throwable reason)
    {
        super(message,reason);
    }
}
