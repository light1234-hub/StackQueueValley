package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a Ground that can be dried out overtime based on the
 * temperature of the tile retrieved from the WeatherAPI
 */
public abstract class DryingWeatherGround extends WeatherGround
{
    private static final double DRYING_REQUIRED = 10.0;
    private double dryingProgress;

    /**
     * Creates an object of the DryingWeatherGround class
     * @param symbol
     * @param name
     * @param originalGround
     */
    protected DryingWeatherGround(char symbol, String name, Ground originalGround)
    {
        super(symbol,name,originalGround);
    }

    /**
     * Updates the ground drying progress each turn, calculates the rate of drying dynamically based
     * on the current temperature of the tile
     * @param location The location of the Ground
     */
    public final void tick (Location location)
    {
        double dryingRate = Math.max(0.5, Math.min(2.0, getTemperature() / 25.0));
        dryingProgress += dryingRate;

        if (dryingProgress >= DRYING_REQUIRED && location.getGround() == this)
        {
            onDry(location);
        }
    }

    /**
     * An abstract method that executes when the ground has finished drying
     * @param location
     */
    protected abstract void onDry(Location location);
}
