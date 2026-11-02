package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Consumable;

/**
 * Action that consumes an item on behalf of an actor.
 */
public class ConsumeAction extends Action {

    private final Consumable item;

    /**
     * Creates a consume action for an item.
     *
     * @param item item to consume
     */
    public ConsumeAction(Consumable item){
        this.item = item;
    }

    /**
     * Consumes the configured item.
     *
     * @param actor actor performing the action
     * @param map current game map
     * @return a description of the consumption result
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (!actor.getInventory().getItems().contains(item.getItem()))
        {
            return "This item is no longer in the inventory";
        }

        String result = item.consume(actor);
        actor.getInventory().remove(item.getItem());
        return result;
    }

    /**
     * Returns the action text shown in the menu.
     *
     * @param actor actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor){
        return actor+ " consume " + item.toString();
    }

}
