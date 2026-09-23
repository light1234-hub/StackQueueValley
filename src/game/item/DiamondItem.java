package game.item;

import edu.monash.fit2099.engine.items.Item;

/**
 * A diamond, found when a geode splits open successfully instead of
 * exploding.
 */
public class DiamondItem extends Item {
    public DiamondItem() {
        super("Diamond", 'd');
        this.makePortable();
    }
}
