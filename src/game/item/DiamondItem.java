package game.item;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.Sellable;
import game.actor.FinanceStatistics;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;


/**
 * A diamond, found when a geode splits open successfully instead of
 * exploding.
 */
public class DiamondItem extends Item implements Sellable
{
    private static final int SALE_PRICE = 5000;
    private static final double THEFT_CHANCE = 0.5;
    private static final Random random = new Random();

    public DiamondItem()
    {
        super("Diamond", 'd');
        this.makePortable();
    }

    private Item stealAnotherItem(Actor farmer)
    {
        List<Item> items = new ArrayList<>();

        for (Item item : farmer.getInventory().getItems())
        {
            if (item != this)
            {
                items.add(item);
            }
        }

        if (items.isEmpty())
        {
            return null;
        }
        else
        {
            return items.get(random.nextInt(items.size()));
        }
    }

    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation)
    {
        seller.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,
                SALE_PRICE);

        String message = seller + " sells a Diamond to the " + merchant + " for "
                + SALE_PRICE + " coins.";

        if (random.nextDouble() < THEFT_CHANCE)
        {
            Item stolen = stealAnotherItem(seller);

            if (stolen != null)
            {
                seller.getInventory().remove(stolen);
                message += " " + merchant + " also steals " +
                            stolen + " from the " + seller;
            }
        }

        return message;
    }
}
