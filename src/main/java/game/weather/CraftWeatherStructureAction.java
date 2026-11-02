package game.weather;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Crafting action for all weather structures
 */
public class CraftWeatherStructureAction extends Action {

    private final WeatherStructurePlan plan;

    public CraftWeatherStructureAction(WeatherStructurePlan plan){
        this.plan = plan;
    }

    @Override
    public String execute(Actor actor, GameMap map){
        if(!plan.consumeMaterials(actor)){
            return "Not enough materials to craft " + plan.displayName() + "; requires " + plan.recipeDescription();
        }

        actor.getInventory().add(new WeatherStructureKit(plan));

        return actor + " craft a " + plan.displayName() + " kit. ";

    }

    @Override
    public String menuDescription(Actor actor){
        return "Craft " + plan.displayName() + " (" + plan.recipeDescription() +")";
    }
}
