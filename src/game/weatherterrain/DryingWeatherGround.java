package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public abstract class DryingWeatherGround extends WeatherGround
{
    private static final double DRYING_REQUIRED = 10.0;
    private double dryingProgress;

    protected DryingWeatherGround(char symbol, String name, Ground originalGround)
    {
        super(symbol,name,originalGround);
    }

    public final void tick (Location location)
    {
        double dryingRate = Math.max(0.5, Math.min(2.0, getTemperature() / 25.0));
        dryingProgress += dryingRate;

        if (dryingProgress >= DRYING_REQUIRED && location.getGround() == this)
        {
            onDry(location);
        }
    }

    protected abstract void onDry(Location location);
}
