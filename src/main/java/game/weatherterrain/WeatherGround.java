package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.weatherblueprint.WeatherHazard;

public abstract class WeatherGround extends Ground implements WeatherHazard
{
    private final Ground originalGround;
    private double temperature = 25.0;

    protected WeatherGround(char symbol, String name, Ground originalGround)
    {
        super(symbol,name);
        this.originalGround = originalGround;
    }

    public final Ground getOriginalGround()
    {
        return originalGround;
    }

    public final void setTemperature(double temperature)
    {
        this.temperature = temperature;
    }

    protected final double getTemperature()
    {
        return temperature;
    }

    @Override
    public boolean isDangerous()
    {
        return true;
    }
}
