package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Sellable;

/**
 * A piece of copper, dropped whenever a copper node is broken.
 */
public class CopperItem extends Item implements Sellable
{
    private static final int SALE_PRICE = 3500;

    public CopperItem()
    {
        super("Copper", 'c');
        this.makePortable();
    }

    /**
     * Processes the sale of a copper item to the merchant
     * @param seller
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the sale
     */
    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation)
    {
        seller.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.INCREASE, SALE_PRICE);

        seller.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 5);

        return seller + " sells Copper to " + merchant + " for " + SALE_PRICE + " coins! "
                + "Hauling the heavy ore exhausts " + seller + " (-5 HP).";
    }
}
