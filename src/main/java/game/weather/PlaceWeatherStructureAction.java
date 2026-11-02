
package game.weather;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.ability.TerrainAbility;

/**
 * Generic action for placing weather structures.
 */
public class PlaceWeatherStructureAction extends Action {

    private final WeatherStructureKit kit;
    private final Location target;
    private final String direction;

    /**
     * A constructor that creates an object of the PlaceWeatherStructureAction class
     * @param kit
     * @param target
     * @param direction
     */
    public PlaceWeatherStructureAction(WeatherStructureKit kit, Location target, String direction) {
        this.kit = kit;
        this.target = target;
        this.direction = direction;
    }
    /**
     * Checks whether the destination supports placement.
     */
    static boolean canPlace(Location target) {
        return !target.containsAnActor()
                && target.getItems().isEmpty()
                && target.getGround().hasAbility(
                TerrainAbility.WEATHER_STRUCTURE_PLACEABLE
        );
    }

    /**
     * Executes the actions, verifies that the actor has the kit and
     * the target location is adjacent and valid.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a descriptive message describing the success or failure of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.getInventory().getItems().contains(kit)) {
            return "You are no longer carrying " + "that weather structure kit.";
        }
        boolean adjacent = false;
        for (Exit exit : map.locationOf(actor).getExits()) {
            if (exit.getDestination() == target) {
                adjacent = true;
                break;
            }
        }
        if (!adjacent || !canPlace(target)) {
            return "Cannot build there; choose empty " + "adjacent Dirt or Soil.";
        }
        WeatherResponseStructure structure = kit.plan().createGround();

        target.setGround(structure);
        structure.onPlaced(target);

        actor.getInventory().remove(kit);
        return actor + " places " + kit.plan().displayName() + " to the " + direction + ".";
    }

    /**
     * Returns a descriptive string for this action in the menu
     * @param actor The actor performing the action.
     * @return the menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Place " + kit.plan().displayName() + " " + direction;
    }
}

