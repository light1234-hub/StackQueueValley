package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Locale;

/**
 * Temporary muddy terrain created after drainage.
 * The mud dries after three world turns and restores
 * the terrain that existed before the rainstorm.
 */
public final class MudGround extends DryingWeatherGround
{

    /**
     * Creates mud terrain.
     *
     * @param originalGround terrain to restore after drying
     */
    public MudGround(Ground originalGround){
        super('m',"Mud",originalGround);
    }

    @Override
    protected void onDry(Location location)
    {
        location.setGround(getOriginalGround());
    }
}
