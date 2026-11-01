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
public class FloodedGround extends Ground implements Drainable, WeatherHazard {

    private static final int EVAPORATED_RATE = 6;
    private int turnsRemaining;
    private final Ground originalGround;

    /**
     * Created flooded terrain while preserving its original ground
     * @param originalGround terrain that existed before flooding
     */
    public FloodedGround(Ground originalGround){
        super('=',"Flooded Ground");
        this.turnsRemaining = EVAPORATED_RATE;
        this.originalGround = originalGround;
    }

    /**
     * advanced natural evaporation
     * @param location The location of the Ground that is flooded
     */
    @Override
    public void tick(Location location){
        turnsRemaining--;
        if(turnsRemaining <= 0){
            location.setGround(new Soil());
        }
    }
    /**
     * Drains the flooded terrain into mud.
     * The original ground is passed to MudGround so it
     * can be restored after the mud dries.
     *
     * @param location location occupied by this flooded ground
     * @return true if drainage succeeds
     */
    @Override
    public boolean drain(Location location){
        if(location.getGround() != this ){
            return false;
        }

        if (location.containsAnActor()){
            return false;
        }
        location.setGround(new MudGround(originalGround));
        return  true;
    }
    /**
     * Refreshes the evaporation countdown after new rainfall.
     */
    public void refresh (){
        turnsRemaining = EVAPORATED_RATE;
    }
    /**
     * Returns the turns remaining before natural evaporation.
     *
     * @return remaining turns
     */
    public int turnsRemaining(){
        return turnsRemaining;
    }

    /**
     * Indicates the flooded terrain is dangerous
     * @return true while the terrain is flooded
     */

    @Override
    public boolean isDangerous(){
        return true;
    }
}
