package game.blueprint;

import edu.monash.fit2099.engine.items.Item;

/**
 * Represents an item that can be consumed and processed into another item.
 */
public interface Processable extends Consumable{

    /**
     * Returns the item represented by this processable object.
     *
     * @return the corresponding item
     */
    Item getItem();
}
