package game.recipe;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Ingredient;
import game.ability.IngredientAbility;
import game.dish.OmeletteDish;
import game.effect.FireEffect;

import java.util.List;
import java.util.Random;

/**
 * A class that represents an Omelette Recipe
 */
public class OmeletteRecipe extends FixedAmountRecipe
{
    private static final double FLARE_CHANCE = 0.3;

    private final Random random = new Random();

    /**
     * An omelette recipe is return as Omelette
     * @return the string Omelette
     */
    @Override
    public String name()
    {
        return "Omelette";
    }

    /**
     * An omelette requires one Animal Product and one Vegetable Product
     * @return a read only list of one Animal Product and one Vegetable product
     */
    @Override
    protected List<IngredientRequirement> requirements()
    {
        return List.of(new IngredientRequirement(IngredientAbility.ANIMAL_PRODUCT,1),
                new IngredientRequirement(IngredientAbility.VEGETABLE,1));
    }

    /**
     * Applies the effect of cooking an Omelette Recipe
     * @param farmer
     * @param station
     * @param ingredients
     * @return a string describing the result of the effect
     */
    @Override
    public String applyCookEffect(Actor farmer, Location station, List<Ingredient> ingredients)
    {
        List<Location> adjacent = AdjacentLocations.from(station);

        if (random.nextDouble() >= FLARE_CHANCE || adjacent.isEmpty())
        {
            return "The pan cooks without flaring";
        }

        Location fireLocation = adjacent.get(random.nextInt(adjacent.size()));
        fireLocation.getGround().addStatus(new FireEffect(2,0,3,5));

        return "The pan flares up and sets a nearby tile on fire!";
    }

    /**
     * Creates a new Omelette dish after cooking the recipe
     * @param ingredients
     * @return a new Omelette Dish object
     */
    @Override
    public Item createDish(List<Ingredient> ingredients)
    {
        return new OmeletteDish();
    }
}
