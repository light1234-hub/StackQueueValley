package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Consumable;
import game.farmeraction.ConsumeAction;

/**
 * A class that represents the behaviour for all cooked dishes
 */
public abstract class DishItem extends Item implements Consumable
{
    /**
     * Creates an object of the DishItem class
     * @param name
     * @param displayChar
     */
    protected DishItem(String name, char displayChar)
    {
        super(name, displayChar);
        makePortable();
    }

    /**
     * A getter that returns the name of the item
     * @return the item
     */
    public final Item getItem()
    {
        return this;
    }

    /**
     * Returns a list of actions that can be performed on the dish item
     * @param owner the actor that owns the item
     * @param map the map where the actor is performing the action on
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map)
    {
        ActionList actions = super.allowableActions(owner,map);
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
