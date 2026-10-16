package game.blueprint;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;

/**
 * An interface representing all items that can be used
 * as a cooking ingredient
 */
public interface Ingredient
{
    /**
     * Applies the effect of the ingredient onto the dish
     * as it goes on
     * @param farmer
     * @param station
     * @return a string describing the effect
     */
    String useEffect(Actor farmer, Location station);

    /**
     * A getter that returns the item that represents the ingredient
     * @return how the ingredient is stored in the players inventory
     */
    Item getItem();
}
