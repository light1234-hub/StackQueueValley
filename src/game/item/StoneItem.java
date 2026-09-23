package game.item;

import edu.monash.fit2099.engine.items.Item;

/**
 * A stone, dropped whenever a rock is broken.
 */
public class StoneItem extends Item {
    public StoneItem() {
        super("Stone", 'o');
        this.makePortable();
    }
}
