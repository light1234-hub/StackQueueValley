package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.ability.ToolAbility;
import game.item.GeodeItem;
import game.terrain.GeodeMechanics;


public class BreakGeodeFromInventoryAction extends Action {
    private final GeodeItem geode;

    public BreakGeodeFromInventoryAction(GeodeItem geode) {
        this.geode = geode;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(ToolAbility.MINE)) {
            return actor + " needs a Pickaxe to break this.";
        }
        actor.getInventory().remove(geode);
        return GeodeMechanics.resolve(actor, map.locationOf(actor));
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " breaks the geode";
    }
}