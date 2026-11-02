
package game.weather;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Portable weather structure kit.
 * Provides placement actions on eligible adjacent terrain.
 */
public class WeatherStructureKit extends Item {

    private final WeatherStructurePlan plan;

    public WeatherStructureKit(WeatherStructurePlan plan) {
        super(plan.displayName() + " Kit", plan.kitSymbol());
        this.plan = plan;
        makePortable();
    }

    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = new ActionList();
        if (!actor.getInventory().getItems().contains(this)) {
            return actions;
        }
        for (Exit exit : map.locationOf(actor).getExits()) {
            Location target = exit.getDestination();
            if (PlaceWeatherStructureAction.canPlace(target)) {actions.add(new PlaceWeatherStructureAction(this, target, exit.getName()));
            }
        }
        return actions;
    }

    WeatherStructurePlan plan() {
        return plan;
    }
}

