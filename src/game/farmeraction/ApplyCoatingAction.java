package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Coatable;
import game.blueprint.Coating;
import game.blueprint.CoatingSource;

/** Applies a coating from a carried or adjacent source to a carried {@link Coatable} item. */
public class ApplyCoatingAction extends Action {
    private final Item targetItem;
    private final Coatable target;
    private final CoatingSource source;
    private final Item sourceItem;
    private final Location sourceLocation;

    /**
     * Creates an action for applying a coating.
     *
     * @param targetItem carried item being coated
     * @param target coatable behavior provided by that item
     * @param source provider of the coating
     * @param sourceItem source item, or {@code null} for a ground or status source
     * @param sourceLocation location of an adjacent source item or ground source;
     *                       {@code null} when the source is carried
     */
    public ApplyCoatingAction(Item targetItem, Coatable target, CoatingSource source,
                              Item sourceItem, Location sourceLocation) {
        this.targetItem = targetItem;
        this.target = target;
        this.source = source;
        this.sourceItem = sourceItem;
        this.sourceLocation = sourceLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!source.isAvailable()) {
            return "The coating source is no longer active.";
        }
        if (sourceItem != null && source.isConsumedOnUse()) {
            if (sourceLocation == null) actor.getInventory().remove(sourceItem);
            else sourceLocation.removeItem(sourceItem);
        }
        Coating coating = source.createCoating();
        target.setCoating(coating);
        return actor + " coats " + targetItem + " with " + coating.getName() + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " coats " + targetItem + " with " + source.createCoating().getName();
    }
}
