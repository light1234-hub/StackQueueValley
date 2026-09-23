package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.farmeraction.BreakGeodeFromInventoryAction;

/**
 * A geode being carried in the farmer's inventory, rather than lying on the
 * ground. It can be broken directly from here -- see
 * {@link BreakGeodeFromInventoryAction}. Uses a different display character
 * to the ground version (game.terrain.Geode) purely so the two are easy to
 * tell apart on screen.
 */
public class GeodeItem extends Item {
    public GeodeItem() {
        super("Geode", 'q');
        this.makePortable();
    }

    /**
     * Offers the option to break this geode while it's being carried.
     *
     * @param owner the actor carrying this geode
     * @param map the map the actor is currently on
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        actions.add(new BreakGeodeFromInventoryAction(this));
        return actions;
    }
}
