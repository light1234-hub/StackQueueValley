
package game.weather;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides available weather structure crafting actions.
 */
public class WeatherCraftingProvider {

    private final List<WeatherStructurePlan> plans;

    /**
     * Creates and registers the available crafting plans.
     */
    public WeatherCraftingProvider() {

        plans = new ArrayList<>();

        plans.add(new WeatherStructurePlan("Drainage Structure", 'd', WeatherStructureType.DRAINAGE, 2, 1));

        plans.add(new WeatherStructurePlan("Wildlife Shelter", 'h', WeatherStructureType.WILDLIFE, 3, 1));

        plans.add(new WeatherStructurePlan("Resource Recovery Structure", 'r', WeatherStructureType.RECOVERY, 1, 2));
    }

    /**
     * Adds crafting actions for recipes the actor can afford.
     * @param actor actor performing crafting
     * @param actions available actions
     */
    public void addAvailableActions(Actor actor, ActionList actions) {
        for (WeatherStructurePlan plan : plans) {
            if (plan.canCraft(actor)) {actions.add(new CraftWeatherStructureAction(plan));
            }
        }
    }
}

