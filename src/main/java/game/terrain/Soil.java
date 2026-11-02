package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.ability.GroundAbility;
import game.ability.TerrainAbility;

/**
 * Tilled ground that can be used as a farm plot.
 */
public class Soil extends Ground {

    /**
     * Creates fertile soil and enables supported planting and terrain interactions.
     */
    public Soil() {
        super(',', "Soil");
        enableAbility(TerrainAbility.CAN_BE_PUDDLE);
        enableAbility(GroundAbility.FERTILE);
        enableAbility(GroundAbility.GROVE_ENRICHABLE);
        enableAbility(GroundAbility.GROVE_SEEDING_PLOT);
        enableAbility(GroundAbility.BLIGHT_INFECTABLE);
        enableAbility(GroundAbility.TRAP_BUILDABLE);
        enableAbility(GroundAbility.COLLAPSE_REPLACEABLE);
        enableAbility(GroundAbility.QUEST_FEATURE_PLACEABLE);
        enableAbility(GroundAbility.RESOURCE_REVEALABLE);
        this.enableAbility(TerrainAbility.CAN_BE_PUDDLE);
        this.enableAbility(GroundAbility.FERTILE);
        this.enableAbility(TerrainAbility.WEATHER_STRUCTURE_PLACEABLE);
    }
}
