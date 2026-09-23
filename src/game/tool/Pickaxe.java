package game.tool;

import edu.monash.fit2099.engine.items.Item;
import game.ability.ToolAbility;

/**
 * A portable tool that grants the ability to break rocks, copper nodes and
 * geodes in the mines.
 */
public class Pickaxe extends Item {

    /**
     * Creates a pickaxe and enables its mining ability.
     */
    public Pickaxe() {
        super("Pickaxe", 'p');
        this.enableAbility(ToolAbility.MINE);
        this.makePortable();
    }
}
