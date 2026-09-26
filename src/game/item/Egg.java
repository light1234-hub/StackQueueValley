package game.item;

import edu.monash.fit2099.engine.items.Item;

/**
 * Represents an Egg that is dropped by feeding a Chicken
 */
public class Egg extends Item
{
    /**
     * A Constructor that creates an object of the Egg class
     */
    public Egg()
    {
        super("Egg",'0');
        this.makePortable();
    }
}
