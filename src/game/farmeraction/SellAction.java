package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Sellable;

public class SellAction extends Action
{
    private final Actor merchant;
    private final Item saleItem;
    private final Sellable sellable;


    public SellAction(Actor merchant, Item saleItem, Sellable sellable)
    {
        this.merchant = merchant;
        this.saleItem = saleItem;
        this.sellable = sellable;
    }

    @Override
    public String execute(Actor actor, GameMap map)
    {
        String message = sellable.sold(actor,merchant,map.locationOf(merchant));
        actor.getInventory().remove(saleItem);

        return message;
    }

    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " sells " + saleItem +
                " to the " + merchant;

    }
}
