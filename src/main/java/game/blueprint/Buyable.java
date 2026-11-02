package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An interface representing all items that can be bought by the player from the merchant
 */
public interface Buyable
{
    /**
     * A getter that returns the cost of buying a singular product
     * @return
     */
    int getBuyCost();

    /**
     * Carries out the logic of a purchase when an item is bought
     * @param buyer
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the purchase
     */
    String bought(Actor buyer, Actor merchant, Location merchantLocation);
}
