package game.recipe;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Ingredient;
import game.ability.IngredientAbility;
import game.blueprint.Tillable;
import game.dish.JamDish;

import java.util.List;

/**
 * A class that represents a Jam Recipe
 */
public class JamRecipe extends UseEverythingRecipe
{
    /**
     * A constructor that initializes that a Jam Recipe
     * requires fruit ingredients
     */
    public JamRecipe()
    {
        super(IngredientAbility.FRUIT);
    }

    /**
     * A Jam recipe is returned as Jam
     * @return the string Jam
     */
    @Override
    public String name()
    {
        return "Jam";
    }

    /**
     * Applies the effect of cooking a Jam Recipe
     * @param farmer
     * @param station
     * @param ingredients
     * @return a string describing the result of the effects being applied
     */
    @Override
    public String applyCookEffect(Actor farmer, Location station, List<Ingredient> ingredients)
    {
        for (Location adjacent : AdjacentLocations.from(station))
        {
            Tillable tillable = adjacent.getGroundAs(Tillable.class);

            if (tillable != null)
            {
                tillable.till(adjacent);
            }
        }

        return "The fruit pulp enriches the surrounding Dirt into Soil";
    }

    /**
     * Creates a new Jam Dish after cooking the recipe
     * @param ingredients
     * @return a new JamDish object
     */
    @Override
    public Item createDish(List<Ingredient> ingredients)
    {
        return new JamDish(ingredients.size());
    }
}
