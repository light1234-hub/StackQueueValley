package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Processable;
import game.farmeraction.ProcessAction;

/**
 * Represents a location in the Map where items can be processed
 */
public abstract class ProcessingFacility extends Ground
{
    /**
     * A constructor that creates an object of the ProcessingFacility class
     * @param name
     * @param displayChar
     */
    protected ProcessingFacility(String name, char displayChar)
    {
        super(displayChar, name);
    }

    /**
     * A method that returns the Processing Actions that can be carried out
     * on an item at a ProcessingFacility
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction)
    {
        ActionList actions = new ActionList();

        for (Item item : actor.getInventory().getItems())
        {
            item.asCapability(Processable.class).ifPresent(processable ->
                    actions.add(new ProcessAction(this,item,processable)));
        }

        return actions;
    }

    /**
     * A method that handles the modifying of the item's name after Processing
     * @param preProcessedProduct
     * @return the item with a modified name to reflect the processing it underwent
     */
    public abstract Item process(Processable preProcessedProduct);
}
