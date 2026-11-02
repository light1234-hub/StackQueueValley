package game.weather;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a weather induced movement cooldown status that restricts
 * an actors ability to move continuously over a set number of turns
 */
public final class WeatherMovementCooldown implements Status
{
    private int turnsRemaining;

    /**
     * A constructor that creates an object of the WeatherMovementCooldown with an initial active state of 0 turns
     */
    public WeatherMovementCooldown()
    {
        turnsRemaining = 0;
    }

    /**
     * Starts the movement cooldown period with a remaining duration of 3 turns
     */
    public void start()
    {
        turnsRemaining = 3;
    }

    /**
     * Checks if the movement cooldown is still active
     * @return true if active, false if no longer active
     */
    public boolean isCoolingDown()
    {
        return turnsRemaining > 0;
    }

    /**
     * Decrements the remaining turns left of the MovementCooldown each turn
     * @param currEntity the entity this status is attached to
     * @param location
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location)
    {
        if (turnsRemaining > 0)
        {
            turnsRemaining--;
        }
    }

    /**
     * Checks whether the status is currently active based on the reamining cooldown
     * @return
     */
    @Override
    public boolean isStatusActive()
    {
        return isCoolingDown();
    }
}
