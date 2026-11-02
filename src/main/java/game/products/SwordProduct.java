package game.products;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Buyable;
import game.item.Sword;

import java.util.Random;

/**
 * Represents a sword product that can be purchased from the merchant
 */
public class SwordProduct implements Buyable
{
    private static final int PURCHASE_PRICE = 3000;
    private static final double SCAM_CHANCE = 0.5;
    private final Random random = new Random();

    /**
     * A getter returning the cost of purchasing a sword
     * @return the price of the sword item
     */
    public int getBuyCost()
    {
        return PURCHASE_PRICE;
    }

    /**
     * Carries out the logic of the sword product purchase after it has been bought
     * @param buyer
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the purchase (successful or scammed)
     */
    @Override
    public String bought(Actor buyer, Actor merchant, Location merchantLocation)
    {
        if (random.nextDouble() > SCAM_CHANCE)
        {
            buyer.getInventory().add(new Sword());
            return "You buy a Sword from the " + merchant
                    + " for " + PURCHASE_PRICE + " coins.";
        }
        else
        {
            return "You got scammed by the Merchant! " +
                    "He takes your coins and hands you nothing!";
        }
    }

    /**
     * Returns a string representation of the product
     * @return a string representing the name of the sword product
     */
    @Override
    public String toString()
    {
        return "Sword";
    }


}
