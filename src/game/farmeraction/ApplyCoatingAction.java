package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Coatable;
import game.blueprint.Coating;

/** Applies a coating to any carried item that implements {@link Coatable}. */
public class ApplyCoatingAction extends Action {
    private final Item targetItem;
    private final Coatable target;
    private final Coating coating;
    private final Item sourceItem;

    /**
     * Creates an action for applying a coating.
     *
     * @param targetItem carried item being coated
     * @param target coatable behavior provided by that item
     * @param coating coating to apply
     * @param sourceItem optional carried source item to consume, or {@code null}
     */
    public ApplyCoatingAction(Item targetItem, Coatable target, Coating coating, Item sourceItem) {
        this.targetItem = targetItem;
        this.target = target;
        this.coating = coating;
        this.sourceItem = sourceItem;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (sourceItem != null) actor.getInventory().remove(sourceItem);
        target.setCoating(coating);
        return actor + " coats " + targetItem + " with " + coating.getName() + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " coats " + targetItem + " with " + coating.getName();
    }
}
