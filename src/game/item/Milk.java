package game.item;

import edu.monash.fit2099.engine.items.Item;

/**
 * Milk produced by a cow.
 */
public class Milk extends Item {

    /**
     * Creates portable milk.
     */
    public Milk(){
        super("Milk",'M');
        this.makePortable();
    }

}
