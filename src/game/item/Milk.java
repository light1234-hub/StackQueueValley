package game.item;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Sellable;

import java.util.Random;


/**
 * Milk produced by a cow.
 */
public class Milk extends Item implements Sellable
{
    private static final int SALE_PRICE = 60;
    private static final double SUCCESSFUL_SALE = 0.5;
    private final Random random = new Random();

    /**
     * Creates portable milk.
     */
    public Milk(){
        super("Milk",'M');
        this.makePortable();
    }

    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation)
    {
        if (random.nextDouble() < SUCCESSFUL_SALE)
        {
            seller.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE
                    ,SALE_PRICE);

            return seller + " sells Milk to the " + merchant + " for "
                    + SALE_PRICE + " coins.";
        }
        else
        {
            return merchant + " takes the Milk from the  " + seller
                    + " without paying.";
        }
    }

}
