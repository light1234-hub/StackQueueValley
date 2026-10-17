package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Ground;
import game.farmeraction.CookAction;
import game.recipe.Recipe;

import java.util.List;

/**
 * A class that represents a Cooking Station where a farmer
 * can cook dishes based on recipes
 */
public class CookingStation extends Ground
{
    private final List<Recipe> recipes;

    /**
     * A constructor that creates an object of the Cooking Station
     * @param recipes
     */
    public CookingStation(List<Recipe> recipes)
    {
        super('K',"Cooking Station");
        this.recipes = List.copyOf(recipes);
    }

    /**
     * Determines whether or not an actor can pass through this terrain
     * @param actor the Actor to check
     * @return false
     */
    @Override
    public boolean canActorEnter(Actor actor)
    {
        return false;
    }

    /**
     * Returns the actions that an actor can perform on the cooking station
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction)
    {
        ActionList actions = super.allowableActions(actor,location,direction);

        if (direction.isEmpty())
        {
            for (Recipe recipe : recipes)
            {
                actions.add(new CookAction(recipe,location));
            }
        }

        return actions;
    }
}
