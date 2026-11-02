package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
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
    private final Ground expectedStationGround;

    /**
     * A constructor that creates an object of the CookAction class
     *
     * @param recipe
     * @param station
     */
    public CookAction(Recipe recipe, Location station)
    {
        this.recipe = recipe;
        this.station = station;
        this.expectedStationGround = station.getGround();
    }

    /**
     * Cooks the recipe for the actor
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return describes the effects of the cooking, whether the dish
     * has been successfully prepared or there aren't enough ingredients to cook
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (station.getGround() != expectedStationGround)
        {
            return "The Cooking Station is currently unavailable";
        }

        if (!isAdjacent(actor, map))
        {
            return "You must be beside the Cooking Station in order to cook";
        }

        Optional<List<Ingredient>> selection = recipe.selectIngredients(actor.getInventory());

        if (selection.isEmpty())
        {
            return "There aren't enough ingredients in your inventory for " + recipe.name();
        }

        List<Ingredient> ingredients = selection.get();
        StringBuilder result = new StringBuilder();

        for (Ingredient ingredient : ingredients)
        {
            result.append(ingredient.useEffect(actor, station)).append('\n');
            actor.getInventory().remove(ingredient.getItem());
        }

        result.append(recipe.applyCookEffect(actor, station, ingredients));

        Item dish = recipe.createDish(ingredients);
        actor.getInventory().add(dish);

        result.append('\n').append(recipe.name()).append(" is ready.");
        return result.toString();
    }

    /**
     * Returns the description of how this action appears in the menu
     *
     * @param actor The actor performing the action.
     * @return a string describing the menu option of a cook action
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " cooks " + recipe.name();
    }

    /**
     * Checks whether an actor is on the same map and
     * directly adjacent to the cooking station
     * @param actor
     * @param map
     * @return true if the actor is adjacent to the station, false if not adjacent
     */
    private boolean isAdjacent(Actor actor, GameMap map)
    {
        if (!map.contains(actor))
        {
            return false;
        }

        Location actorLocation = map.locationOf(actor);

        for (Exit exit : actorLocation.getExits())
        {
            if (exit.getDestination() == station)
            {
                return true;
            }
        }

        return false;
    }
}
