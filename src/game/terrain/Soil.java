package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.ability.TerrainAbility;

/**
 * Tilled ground that can be used as a farm plot.
 */
public class Soil extends Ground {

    /**
     * A class representing puddle on the ground.
     */
    /**
     * Creates tilled soil.
     */
    public Soil() {
        super(',', "Soil");
        this.enableAbility(TerrainAbility.CAN_BE_PUDDLE);
    }
}
