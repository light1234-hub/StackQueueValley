package game.weather;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public final class SoakedStatus implements Status
{
    private int turnsRemaining;

    public SoakedStatus(int duration)
    {
        turnsRemaining = Math.max(0,duration);
    }

    public void tickStatus(GameEntity entity, Location location)
    {
        if (turnsRemaining > 0)
        {
            turnsRemaining--;
        }
    }

    @Override
    public boolean isStatusActive()
    {
        return turnsRemaining > 0;
    }
}
