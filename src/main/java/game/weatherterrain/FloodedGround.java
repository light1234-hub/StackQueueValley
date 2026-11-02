package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.Soil;
import game.weatherblueprint.Drainable;
import game.weatherblueprint.WeatherHazard;

import java.util.Locale;

/**
 * Temporary flooded terrain created by a rainstrom
 * without drainage,floodwater evaporates after 6 turns and leaves behind soil
 * when drained, the terrain becomes Mudground, which eventually restores to the original state
 *
 */
public final class FloodedGround extends DryingWeatherGround implements Drainable
{
    public FloodedGround(Ground originaGround)
    {
        super('=', "Flooded Ground",originaGround);
    }

    @Override
    protected void onDry(Location location)
    {
        location.setGround(new MudGround(getOriginalGround()));
    }

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
