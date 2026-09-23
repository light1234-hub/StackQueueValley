package game.item;

import edu.monash.fit2099.engine.items.Item;

/**
 * A piece of copper, dropped whenever a copper node is broken.
 */
public class CopperItem extends Item {
    public CopperItem() {
        super("Copper", 'c');
        this.makePortable();
    }
}
