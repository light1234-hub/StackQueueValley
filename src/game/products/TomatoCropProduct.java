package game.products;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.GroundAbility;
import game.actor.FinanceStatistics;
import game.blueprint.Harvestable;
import game.blueprint.Buyable;
import game.terrain.TomatoCrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a Tomato crop that can be purchased from the merchant
 */
public class TomatoCropProduct implements Buyable
{
    private static final int PURCHASE_PRICE = 500;
    private static final double KINDNESS_CHANCE = 0.5;
    private static final int ADJACENT_RADIUS = 1;
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
     * @param buyer
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the purchase (successful, refund or free tomatos)
     */
    @Override
    public String bought(Actor buyer, Actor merchant, Location merchantLocation)
    {
        List<Location> plantableLocations = getPlantableLocations(merchantLocation);

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
     * Scans all nearby locations around a certain point to find all plantable locations
     * @param merchantLocation
     * @return a list of plantable locations
     */
    private List<Location> getPlantableLocations(Location merchantLocation)
    {
        List<Location> plantableLocations = new ArrayList<>();
        List<Location> nearbyLocations = merchantLocation.getNearbyLocations(ADJACENT_RADIUS);

        for (Location nearby : nearbyLocations)
        {
            boolean isFertile = nearby.getGround().hasAbility(GroundAbility.FERTILE);
            boolean hasNoCrop = (nearby.getGroundAs(Harvestable.class) == null);

            if (isFertile && hasNoCrop)
            {
                plantableLocations.add(nearby);
            }
        }

        return plantableLocations;

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
