package game.weather;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a temporary soaked status that is applied to actors
 * as a result of weather events like the RainStorm or terrain like
 * flooded ground or mud.
 */
public final class SoakedStatus implements Status
{
    private int turnsRemaining;

    /**
     * A constructor that creates an object of the SoakedStatus class with a specified duration
     * @param duration
     */
    public SoakedStatus(int duration)
    {
        turnsRemaining = Math.max(0,duration);
    }

    /**
     * Updates the status on each turn, reducing the remaining duration of the applied Soaked Status
     * @param entity the entity this status is attached to
     * @param location
     */
    public void tickStatus(GameEntity entity, Location location)
    {
        if (turnsRemaining > 0)
        {
            turnsRemaining--;
        }
    }

    /**
     * Checks whether the soaked status is still active
     * @return true if active, false if no longer active
     */
    @Override
    public boolean isStatusActive()
    {
        return turnsRemaining > 0;
    }
}
