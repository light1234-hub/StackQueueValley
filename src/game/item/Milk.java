package game.item;

import edu.monash.fit2099.engine.items.Item;

/**
 * Represents Milk that is dropped by feeding a Cow
 */
public class Milk extends Item
{
    /**
     * A Constructor that creates an object of the Milk class
     */
    public Milk()
    {
        super("Milk",'M');
        this.makePortable();
    }
}
