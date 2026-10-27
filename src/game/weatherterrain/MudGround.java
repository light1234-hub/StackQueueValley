package game.weatherterrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Locale;

/**
 * Temporary muddy terrain created after drainage.
 * The mud dries after three world turns and restores
 * the terrain that existed before the rainstorm.
 */
public class MudGround extends Ground {
    private static final int DRYING_TURN = 3;

    private final Ground originalGround;
    private int turnsRemaining;

    /**
     * Creates mud terrain.
     *
     * @param originalGround terrain to restore after drying
     */
    public MudGround(Ground originalGround){
        super('m',"Mud");

        this.originalGround = originalGround;
        this.turnsRemaining = DRYING_TURN;
    }

    /**
     * Mud drying time tick
     * @param location location occupied by this mud
     */

    @Override
    public void tick(Location location){
        turnsRemaining--;
        if(turnsRemaining <= 0){
            location.setGround(originalGround);
        }
    }

    /**
     * Returns the number of turns
     */
    public int turnsRemaining(){
        return turnsRemaining;
    }

}
