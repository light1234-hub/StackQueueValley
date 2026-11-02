package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.item.GeodeItem;
import game.terrain.Dirt;

/**
 * Picks up a geode lying on the ground: the tile becomes dirt (same as when
 * a geode is broken) and a GeodeItem is added to the actor's inventory
 * instead of resolving the break immediately. The item's own action lets
 * the farmer break it later -- see BreakGeodeFromInventoryAction.
 */
public class PickUpGeodeAction extends Action {
    private final Location location;

    public PickUpGeodeAction(Location location) {
        this.location = location;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        location.setGround(new Dirt());
        actor.getInventory().add(new GeodeItem());
        return actor + " picks up the geode.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " picks up the geode";
    }
}
