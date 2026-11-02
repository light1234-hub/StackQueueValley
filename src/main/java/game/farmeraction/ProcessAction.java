package game.farmeraction;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Processable;

/**
 * Action that replaces a processable inventory item with its processed form.
 */
public class ProcessAction extends Action {

    private final Processable oriItem, proItem;

    /**
     * Creates a processing action.
     *
     * @param oriItem original item
     * @param proItem processed item
     */
    public ProcessAction(Processable oriItem, Processable proItem) {
        this.oriItem = oriItem;
        this.proItem = proItem;
    }

    /**
     * Replaces the original item in the actor's inventory.
     *
     * @param actor actor performing the action
     * @param map current game map
     * @return a description of the processing result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.getInventory().remove(oriItem.getItem());
        actor.getInventory().add(proItem.getItem());

        return "Successfully processed!";
    }

    /**
     * Returns the action text shown in the menu.
     *
     * @param actor actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " processes " + oriItem + " into "+proItem;
    }
}
