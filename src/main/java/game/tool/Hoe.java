package game.tool;

import edu.monash.fit2099.engine.items.Item;
import game.ability.ToolAbility;

/**
 * A portable tool that grants the ability to till ground.
 */
public class Hoe extends Item {

    /**
     * Creates a hoe and enables its tilling ability.
     */
    public Hoe(){
        super("Hoe", 'h');
        this.enableAbility(ToolAbility.TILL);
        this.makePortable();
    }
}
