package game.item;

import edu.monash.fit2099.engine.items.Item;
import game.ability.AlchemyAbility;

/** A lure placed by the Taming Cauldron. Not portable: it can only be used up by a creature. */
public class Lure extends Item {
    public Lure() {
        super("Lure", '*');
        enableAbility(AlchemyAbility.LURE);
    }
}