package game.weather;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public final class WeatherMovementCooldown implements Status
{
    private int turnsRemaining;

    public WeatherMovementCooldown()
    {
        turnsRemaining = 0;
    }

    public void start()
    {
        turnsRemaining = 3;
    }

    public boolean isCoolingDown()
    {
        return turnsRemaining > 0;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location)
    {
        if (turnsRemaining > 0)
        {
            turnsRemaining--;
        }
    }

    @Override
    public boolean isStatusActive()
    {
        return isCoolingDown();
    }
}
