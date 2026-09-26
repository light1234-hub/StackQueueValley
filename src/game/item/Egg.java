package game.item;

import edu.monash.fit2099.engine.items.Item;

/**
 * An egg produced by a chicken.
 */
public class Egg extends Item {

    /**
     * Creates a portable egg.
     */
    public Egg(){
        super("Egg",'0');
        this.makePortable();
    }
}
