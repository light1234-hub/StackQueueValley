package game.item;

import edu.monash.fit2099.engine.items.Item;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.Chicken;
import game.actor.FinanceStatistics;
import game.blueprint.Sellable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An egg produced by a chicken.
 */
public class Egg extends Item implements Sellable
{
    private static final int SALE_PRICE = 40;
    private static final double CHICKEN_DROP_CHANCE = 0.5;
    private static final int ADJACENT_RADIUS = 1;
    private final Random random = new Random();


    /**
     * Creates a portable egg.
     */
    public Egg(){
        super("Egg",'0');
        this.makePortable();
    }

    private String spawnChicken(Location merchantLocation)
    {
        Chicken chicken = new Chicken();

        List<Location> adjacentLocations = merchantLocation.getNearbyLocations(ADJACENT_RADIUS);
        List<Location> validLocations = new java.util.ArrayList<>();

        for (Location location : adjacentLocations)
        {
            if (location.canActorEnter(chicken))
            {
                validLocations.add(location);
            }
        }

        if (validLocations.isEmpty())
        {
            return "There is no space for the Chicken to spawn.";
        }
        else
        {
            Location spawnPoint = validLocations.get(random.nextInt(validLocations.size()));

            try
            {
                spawnPoint.addActor(chicken);
                return "The merchant releases a Chicken into the Map!";
            }
            catch (GameEngineException e)
            {
                return "There is no space for the Chicken to spawn.";
            }
        }
    }

    @Override
    public String sold(Actor seller, Actor merchant, Location merchantLocation)
    {
        seller.modifyStatistic(FinanceStatistics.COINS,StatisticOperations.INCREASE,
                SALE_PRICE);


        String message = seller + " sells an Egg to the " + merchant + " for "
                + SALE_PRICE + " coins.";

        if (random.nextDouble() > CHICKEN_DROP_CHANCE)
        {
            message += " " + spawnChicken(merchantLocation);
        }

        return message;

    }

}
