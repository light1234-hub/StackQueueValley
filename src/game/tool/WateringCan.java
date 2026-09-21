package game.tool;

import edu.monash.fit2099.engine.items.Item;
import game.ability.ToolAbility;

/**
 * A portable tool that grants the ability to water ground.
 */
public class WateringCan extends Item {

    /**
     * Creates a watering can and enables its watering ability.
     */
    public WateringCan(){
        super("Watering Can",'w');
        this.enableAbility(ToolAbility.WATER);
        this.makePortable();
    }
}
