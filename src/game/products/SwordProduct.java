package game.products;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Buyable;
import game.item.Sword;

import java.util.Random;

public class SwordProduct implements Buyable
{
    private static final int PURCHASE_PRICE = 3000;
    private static final double SCAM_CHANCE = 0.5;
    private final Random random = new Random();

    public int getBuyCost()
    {
        return PURCHASE_PRICE;
    }

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

    @Override
    public String toString()
    {
        return "Sword";
    }


}
