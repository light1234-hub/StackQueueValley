package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.ability.TerrainAbility;
import game.ability.GroundAbility;

/**
 * Tilled ground that accepts planted and self-spreading crops.
 * Soil also retains its existing terrain and weather capabilities.
 */
public class Soil extends Ground implements PlantableGround {

    /**
     * Creates tilled, plantable soil and enables its existing terrain abilities.
     */
    public Soil() {
        super(',', "Soil");
        this.enableAbility(TerrainAbility.CAN_BE_PUDDLE);
        this.enableAbility(GroundAbility.FERTILE);
        this.enableAbility(TerrainAbility.WEATHER_STRUCTURE_PLACEABLE);
    }
}
