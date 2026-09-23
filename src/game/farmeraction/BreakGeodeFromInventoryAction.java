package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.item.GeodeItem;
import game.terrain.GeodeMechanics;

/**
 * Breaks a geode directly from the farmer's inventory. Per the spec, the
 * explosion (if that's the outcome) is centred on the farmer's own current
 * location rather than wherever the geode was originally found.
 */
public class BreakGeodeFromInventoryAction extends Action {
    private final GeodeItem geode;

    public BreakGeodeFromInventoryAction(GeodeItem geode) {
        this.geode = geode;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        actor.getInventory().remove(geode);
        return GeodeMechanics.resolve(actor, map.locationOf(actor));
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " breaks the geode";
    }
}
