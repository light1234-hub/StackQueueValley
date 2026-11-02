package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.Soil;
import game.weatherblueprint.Drainable;
import game.weatherblueprint.WeatherHazard;

import java.util.Locale;

/**
 * Temporary flooded terrain created by a rainstorm
 * without drainage,floodwater evaporates into mud after 10 turns, taking a further
 * 10 turns before returning back to its original ground.
 * When drained, the flooded ground immediately returns back to the original ground without
 * reaching the mud state
 *
 */
public final class FloodedGround extends DryingWeatherGround implements Drainable
{
    /**
     * A constructor that creates an object of the FloodedGround class
     * @param originaGround
     */
    public FloodedGround(Ground originaGround)
    {
        super('=', "Flooded Ground",originaGround);
    }

    /**
     * On drying, the flooded ground first becomes mud, before eventually drying into the original terrain
     * @param location
     */
    @Override
    protected void onDry(Location location)
    {
        location.setGround(new MudGround(getOriginalGround()));
    }

    /**
     * When the ground is drained, the flooded ground immediately returns back to its original terrain
     * without reaching the mud state
     * @param location location containing the terrain
     * @return the original ground
     */
    @Override
    public boolean drain(Location location)
    {
        if (location.getGround() != this)
        {
            return false;
        }
        location.setGround(getOriginalGround());
        return true;
    }
}
