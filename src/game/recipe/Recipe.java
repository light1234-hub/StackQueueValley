package game.recipe;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Ingredient;

import java.util.List;
import java.util.Optional;

/**
 * An interface that represents all recipes that are known by the Cooking Station
 */
public interface Recipe
{
    /**
     * Returns the name of the current recipe
     * @return a string describing the recipes name
     */
    String name();

    /**
     * Selects all the ingredients that are needed for this specific recipe
     * @param inventory
     * @return A list of ingredients or empty if the ingredients are not in the player's inventory
     */
    Optional<List<Ingredient>> selectIngredients(Inventory inventory);

    /**
     * Applies the effect of cooking this recipe after its ingredients are used up
     * @param farmer
     * @param station
     * @param ingredients
     * @return a string describing the result of the cooking effect
     */
    String applyCookEffect(Actor farmer, Location station, List<Ingredient> ingredients);

    /**
     * Creates a new dish that is produced after cooking this recipe
     * @param ingredients
     * @return a new dish item
     */
    Item createDish(List<Ingredient> ingredients);
}
