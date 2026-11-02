package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Consumable;

/**
 * Action that allows an actor to consume a consumable from the ground
 */
public class EatGroundFoodAction extends Action {
    private final Consumable food;
    private final Location location;

    /**
     * creates an action for consuming food from a location
     * @param food consumable to eat
     * @param location Location containing the item
     */
    public EatGroundFoodAction(Consumable food,Location location){
        this.food=food;
        this.location = location;
    }

    /**
     * Consumes the item and removes it from the ground
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return result of consuming food
     */
    @Override
    public String execute(Actor actor, GameMap map){
        String result = food.consume(actor);
        location.removeItem(food.getItem());
        return result;
    }

    /**
     * Returns the descriptons of this action
     * @param actor The actor performing the action.
     * @return action description
     */
    @Override
    public String menuDescription(Actor actor){
        return  actor + " eat "+food.getItem();
    }
}
