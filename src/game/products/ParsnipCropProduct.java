package game.products;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.GroundAbility;
import game.actor.FinanceStatistics;
import game.blueprint.Harvestable;
import game.blueprint.Buyable;
import game.item.ParsnipItem;
import game.terrain.ParsnipCrop;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Parsnip crop that can be purchased from the merchant
 */
public class ParsnipCropProduct implements Buyable
{
    private static final int PURCHASE_PRICE = 250;
    private static final int PARSNIPS_DROPPED = 3;
    private static final int ADJACENT_RADIUS = 1;

    private final Random random = new Random();

    /**
     * A getter that returns the cost of the purchase
     * @return the cost of buying a parsnip crop
     */
    @Override
    public int getBuyCost()
    {
        return PURCHASE_PRICE;
    }

    /**
     * Scans all nearby locations around a certain point to find all plantable locations
     * @param location
     * @return a list of plantable locations
     */
    private List<Location> plantableLocations(Location location)
    {
        List<Location> plantableLocations = new ArrayList<>();
        List<Location> nearbyLocations = location.getNearbyLocations(ADJACENT_RADIUS);

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
     * Drops a set number of parsnips into randomly selected tiles
     * that are adjacent to the planted parsnip
     * @param cropLocation
     */
    private void dropParsnipsNearby(Location cropLocation)
    {
        List<Location> adjacentLocations = cropLocation.getNearbyLocations(ADJACENT_RADIUS);

        if (adjacentLocations.isEmpty())
        {
            return;
        }
        else
        {
            for (int i = 0; i < PARSNIPS_DROPPED; i++)
            {
                Location dropLocation = adjacentLocations.get(random.nextInt(adjacentLocations.size()));
                dropLocation.addItem(new ParsnipItem());
            }
        }
    }

    /**
     * Carries out the logic of the parsnip crop purchase after it has been bought
     * @param buyer
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the purchase (successful or refund)
     */
    @Override
    public String bought(Actor buyer, Actor merchant, Location merchantLocation)
    {
        List<Location> plantableLocations = plantableLocations(merchantLocation);

        if (plantableLocations.isEmpty())
        {
            buyer.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,PURCHASE_PRICE);
            return merchant + " can't find a suitable place to plant the Parsnip Crop and refunds "
                    + "the " + buyer;
        }

        Location plantLocation = plantableLocations.get(random.nextInt(plantableLocations.size()));

        plantLocation.setGround(new ParsnipCrop());
        dropParsnipsNearby(plantLocation);

        return merchant + " plants a Parsnip Crop costing you " + PURCHASE_PRICE;
    }

    /**
     * Returns a string representation of the product
     * @return a string representing the name of the parsnip crop product
     */
    @Override
    public String toString()
    {
        return "Parsnip crop";
    }
}
