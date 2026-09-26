package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.farmeraction.ConsumeAction;
import game.blueprint.Consumable;

/**
 * An item that can be consumed by the Farmer
 */
public abstract class ConsumableItem extends Item implements Consumable
{
    /**
     * An object that creates an object of the ConsumableItem class
     * @param name
     * @param displaycharacter
     */
    public ConsumableItem(String name, char displaycharacter)
    {
        super(name,displaycharacter);
        this.makePortable();
    }

    /**
     * A method that returns the consume actions that can be performed on this item
     * @param owner the actor that owns the item
     * @param map the map where the actor is performing the action on
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map)
    {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
