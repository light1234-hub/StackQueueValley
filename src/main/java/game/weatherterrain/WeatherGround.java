package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.weatherblueprint.WeatherHazard;

/**
 * An abstract base class that represents weather-affected ground terrain
 */
public abstract class WeatherGround extends Ground implements WeatherHazard
{
    private final Ground originalGround;
    private double temperature = 25.0;

    /**
     * A constructor that creates an object of the WeatherGround class
     * @param symbol
     * @param name
     * @param originalGround
     */
    protected WeatherGround(char symbol, String name, Ground originalGround)
    {
        super(symbol,name);
        this.originalGround = originalGround;
    }

    /**
     * A getter that returns the original ground of the weather affected ground
     * @return the original terrain
     */
    public final Ground getOriginalGround()
    {
        return originalGround;
    }

    /**
     * A setter that sets the temperature of the weather affected ground
     * @param temperature
     */
    public final void setTemperature(double temperature)
    {
        this.temperature = temperature;
    }

    /**
     * A getter that returns the temperature of the weather affected terrain
     * @return the temperature
     */
    protected final double getTemperature()
    {
        return temperature;
    }

    /**
     * Checks whether the weather affected terrain is dangerous
     * @return true
     */
    @Override
    public boolean isDangerous()
    {
        return true;
    }
}
