package game.item;

import edu.monash.fit2099.engine.items.Item;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.Chicken;
import game.actor.FinanceStatistics;
import game.blueprint.Ingredient;
import game.ability.IngredientAbility;
import game.recipe.AdjacentLocations;
import game.blueprint.Sellable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An egg produced by a chicken.
 */
public class Egg extends Item implements Sellable, Ingredient
{
    private static final int SALE_PRICE = 40;
    private static final double CHICKEN_DROP_CHANCE = 0.5;
    private static final int ADJACENT_RADIUS = 1;
    private static final double FERTILE_CHANCE = 0.25;
    private final Random random = new Random();


    /**
     * Creates a portable egg.
     */
    public Egg(){
        super("Egg",'0');
        this.makePortable();
    }

    /**
     * Spawns a new chicken in one of the surrounding tiles from the Merchant
     * @param merchantLocation
     * @return a string describing the result of the attempt to spawn a chicken
     */
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

    /**
     * Sells an egg to the Merchant
     * @param seller
     * @param merchant
     * @param merchantLocation
     * @return a string describing the result of the sale
     */
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

    /**
     * A getter that returns the ingredient category of the Egg
     * @return the ingredient category Animal Product
     */
    @Override
    public IngredientAbility category()
    {
        return IngredientAbility.ANIMAL_PRODUCT;
    }

    /**
     * Applies the effect of adding an Egg to the dish
     * @param farmer
     * @param station
     * @return a string describing the result of using an Egg as an ingredient
     */
    @Override
    public String useEffect(Actor farmer, Location station)
    {
        if (random.nextDouble() >= FERTILE_CHANCE)
        {
            return "The egg is used in the dish";
        }

        Chicken chicken = new Chicken();
        List<Location> locations = new ArrayList<>();

        for (Location adjacent : AdjacentLocations.from(station))
        {
            if (adjacent.canActorEnter(chicken))
            {
                locations.add(adjacent);
            }
        }

        if (locations.isEmpty())
        {
            return "A chick tries to hatch but there isn't any room for it.";
        }

        Location hatchLocation = locations.get(random.nextInt(locations.size()));

        try
        {
            hatchLocation.addActor(chicken);
            return "A chicken hatches next to the station!";
        } catch (GameEngineException e)
        {
            return "A chick tries to hatch but there isn't any room for it.";
        }
    }

    /**
     * A getter that returns the item representation of this ingredient
     * @return this egg
     */
    @Override
    public Item getItem()
    {
        return this;
    }
}
