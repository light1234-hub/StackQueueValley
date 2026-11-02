package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An interface representing all items that can be sold by the player to the merchant
 */
public interface Sellable
{
    /**
     * Carries out the logic of a sale transaction when the item is sold
     * @param seller
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the sale
     */
    String sold(Actor seller, Actor merchant, Location merchantLocation);
}
