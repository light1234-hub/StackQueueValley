package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.item.ConsumableItem;

/**
 * An action for consuming an item from the inventory
 */
public class ConsumeAction extends Action
{
    private final ConsumableItem item;

    /**
     * Constructor that creates an object of the ConsumeAction class
     * @param item
     */
    public ConsumeAction(ConsumableItem item)
    {
        this.item = item;
    }

    /**
     * A method that carries out the action of consuming an item from the inventory
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a String describing the consumption of the item
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        item.applyEffect(actor);
        String message = item.consume(actor);
        actor.getInventory().remove(item);
        return message;
    }

    /**
     * A method that describes the act of consuming an item
     * @param actor The actor performing the action.
     * @return a String representing the consumption of said item in the menu
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " eats the " + item;
    }
}
