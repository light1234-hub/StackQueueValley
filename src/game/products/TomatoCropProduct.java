package game.products;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Buyable;
import game.products.PlantingLocations;
import game.terrain.TomatoCrop;

import java.util.List;
import java.util.Random;

/**
 * Represents a Tomato crop that can be purchased from the merchant
 */
public class TomatoCropProduct implements Buyable
{
    /** Price charged when the merchant does not plant the crop for free. */
    private static final int PURCHASE_PRICE = 500;
    /** Probability that the merchant waives the crop's price after planting. */
    private static final double KINDNESS_CHANCE = 0.5;
    /** Random source used to select a planting location and kindness outcome. */
    private final Random random = new Random();

    /**
     * A getter that returns the cost of the purchase
     * @return the cost of buying a tomato crop
     */
    @Override
    public int getBuyCost()
    {
        return PURCHASE_PRICE;
    }

    /**
     * Carries out the logic of the tomato crop product purchase after it has been bought
     * @param buyer actor purchasing the crop
     * @param merchant merchant completing the transaction
     * @param merchantLocation location used to find nearby plantable ground
     * @return description of planting, refund, or free-crop outcome
     */
    @Override
    public String bought(Actor buyer, Actor merchant, Location merchantLocation)
    {
        List<Location> plantableLocations = PlantingLocations.findPlantableLocations(merchantLocation);

        if (plantableLocations.isEmpty())
        {
            buyer.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,PURCHASE_PRICE);

            return merchant + " can't find a suitable place to plant the Tomato Crop and refunds "
                    + "the " + buyer;
        }

        Location plantLocation = plantableLocations.get(random.nextInt(plantableLocations.size()));

        plantLocation.setGround(new TomatoCrop());

        if (random.nextDouble() < KINDNESS_CHANCE)
        {
            buyer.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,PURCHASE_PRICE);

            return merchant + " plants a Tomato Crop for free!";
        }
        else
        {
            return merchant + " plants a Tomato Crop costing you " + PURCHASE_PRICE;
        }

    }

    /**
     * Returns a string representation of the product
     * @return a string representing the name of the tomato crop product
     */
    @Override
    public String toString()
    {
        return "Tomato crop";
    }
}
