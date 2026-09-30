package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.ProcessAction;
import game.blueprint.Processable;
import game.item.DriedItem;

/**
 * Ground that offers processing actions for processable inventory items.
 */
public class DryingRack extends Ground {

    /**
     * Creates a drying rack.
     */
    public DryingRack(){
        super('D',"DryingRack");
    }

    /**
     * Adds processing actions for each processable item in the actor's inventory.
     *
     * @param actor actor interacting with the drying rack
     * @param location drying rack location
     * @param direction direction from the actor to the rack
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction){
        ActionList actions = super.allowableActions(actor,location,direction);

        for(Processable processableItem: actor.getInventory().getItemsAs(Processable.class)){

            Processable driedItem = new DriedItem(processableItem);

            actions.add(new ProcessAction(processableItem,driedItem));
        }
        return actions;
    }
}
