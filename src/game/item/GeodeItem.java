package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.ability.ToolAbility;
import game.farmeraction.BreakGeodeFromInventoryAction;


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
        if (owner.hasAbility(ToolAbility.MINE)) {
            actions.add(new BreakGeodeFromInventoryAction(this));
        }
        return actions;
    }
}