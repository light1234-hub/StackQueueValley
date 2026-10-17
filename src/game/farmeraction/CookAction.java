package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Ingredient;
import game.recipe.Recipe;

import java.util.List;
import java.util.Optional;

/**
 * A class that represents an action that allows the farmer to cook a dish
 */
public class CookAction extends Action
{
    private final Recipe recipe;
    private final Location station;

    /**
     * A constructor that creates an object of the CookAction class
     * @param recipe
     * @param station
     */
    public CookAction(Recipe recipe, Location station)
    {
        this.recipe = recipe;
        this.station = station;
    }

    /**
     * Cooks the recipe for the actor
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return describes the effects of the cooking, whether the dish
     * has been successfully prepared or there aren't enough ingredients to cook
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        Optional<List<Ingredient>> selection = recipe.selectIngredients(actor.getInventory());

        if (selection.isEmpty())
        {
            return "There aren't enough ingredients in your inventory for " + recipe.name();
        }

        List<Ingredient> ingredients = selection.get();
        StringBuilder result = new StringBuilder();

        for (Ingredient ingredient : ingredients)
        {
            result.append(ingredient.useEffect(actor,station)).append('\n');
            actor.getInventory().remove(ingredient.getItem());
        }

        result.append(recipe.applyCookEffect(actor,station,ingredients));

        Item dish = recipe.createDish(ingredients);
        actor.getInventory().add(dish);

        result.append('\n').append(recipe.name()).append(" is ready.");
        return result.toString();
    }

    /**
     * Returns the description of how this action appears in the menu
     * @param actor The actor performing the action.
     * @return a string describing the menu option of a cook action
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " cooks " + recipe.name();
    }
}
