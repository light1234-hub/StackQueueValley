package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Sellable;

/**
 * Represents an action that sells an item to the merchant
 */
public class SellAction extends Action
{
    private final Actor merchant;
    private final Item saleItem;
    private final Sellable sellable;


    /**
     * A constructor that creates an object of the sell action
     * @param merchant
     * @param saleItem
     * @param sellable
     */
    public SellAction(Actor merchant, Item saleItem, Sellable sellable)
    {
        this.merchant = merchant;
        this.saleItem = saleItem;
        this.sellable = sellable;
    }

    /**
     * Sells the item to the merchant and triggers the logic to handle
     * the addition of coins and the loss of the item from the players inventory
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        String message = sellable.sold(actor,merchant,map.locationOf(merchant));
        actor.getInventory().remove(saleItem);

        return message;
    }

    /**
     * A string describing how the menu description would look like
     * @param actor The actor performing the action.
     * @return a description of the sell action for the menu
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " sells " + saleItem +
                " to the " + merchant;

    }
}
