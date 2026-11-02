package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.ability.GroundAbility;

/**
 * A supply camp established by rescued survivors. Survivors use the camp as a
 * destination for collected resources and as a base for periodic farm repairs.
 */
public class SurvivorCampGround extends Ground {

    /** Creates a camp and marks it as a survivor settlement location. */
    public SurvivorCampGround() {
        super('S', "Survivor Supply Camp");
        enableAbility(GroundAbility.SURVIVOR_CAMP);
    }
}
