package game.recipe;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Ingredient;
import game.ability.IngredientAbility;
import game.dish.HarvestFeast;

import java.util.List;
import java.util.Random;

/**
 * A class that represents a Harvest Feast recipe
 */
public class HarvestFeastRecipe extends FixedAmountRecipe
{
    private final Random random = new Random();

    /**
     * A Harvest Feast recipe is returned as Harvest Feast
     * @return the string Harvest Feast
     */
    @Override
    public String name()
    {
        return "Harvest Feast";
    }

    /**
     * A Harvest Feast requires 5 products of each ingredient type (Fruit, Animal and Vegetable)
     * @return a read only list of 5 fruit,animal and vegetable products
     */
    @Override
    protected List<IngredientRequirement> requirements()
    {
        return List.of(new IngredientRequirement(IngredientAbility.FRUIT,5),
                new IngredientRequirement(IngredientAbility.ANIMAL_PRODUCT,5),
                new IngredientRequirement(IngredientAbility.VEGETABLE,5));
    }

    /**
     * Applies the effect of cooking a Harvest Feast recipe
     * @param farmer
     * @param station
     * @param ingredients
     * @return a string describing the result of the effect
     */
    @Override
    public String applyCookEffect(Actor farmer, Location station, List<Ingredient> ingredients)
    {
        List<Location> adjacent = AdjacentLocations.from(station);

        if (adjacent.isEmpty())
        {
            return "The Harvest Feast is too large to share to nearby tiles";
        }

        Location serveLocation = adjacent.get(random.nextInt(adjacent.size()));
        serveLocation.addItem(new HarvestFeast());

        return "A second Harvest Feast is set down on an adjacent tile";
    }

    /**
     * Creates a new Harvest Feast dish after cooking the recipe
     * @param ingredients
     * @return a new Harvest Feast Dish object
     */
    @Override
    public Item createDish(List<Ingredient> ingredients)
    {
        return new HarvestFeast();
    }
}
