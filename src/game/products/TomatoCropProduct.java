package game.products;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.blueprint.Harvestable;
import game.blueprint.Buyable;
import game.terrain.TomatoCrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TomatoCropProduct implements Buyable
{
    private static final int PURCHASE_PRICE = 500;
    private static final double KINDNESS_CHANCE = 0.5;
    private static final int ADJACENT_RADIUS = 1;
    private final Random random = new Random();

    @Override
    public int getBuyCost()
    {
        return PURCHASE_PRICE;
    }

    @Override
    public String bought(Actor buyer, Actor merchant, Location merchantLocation)
    {
        List<Location> nearbyLocations = merchantLocation.getNearbyLocations(ADJACENT_RADIUS);
        List<Location> plantableLocations = new ArrayList<>();

        for (Location nearby : nearbyLocations)
        {
            if (nearby.getGroundAs(Harvestable.class) == null)
            {
                plantableLocations.add(nearby);
            }
        }

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

    @Override
    public String toString()
    {
        return "Tomato crop";
    }
}
