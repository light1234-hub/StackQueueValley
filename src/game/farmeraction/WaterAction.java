package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.ToolAbility;

import game.blueprint.Waterable;

/**
 * Action that waters ground using a watering can and stamina.
 */
public class WaterAction extends Action{
    private final Waterable targetground;
    private final Location targetLocation;
    /**
     * Creates a watering action for ground.
     *
     * @param targetground ground to water
     */
    public WaterAction(Waterable targetground, Location targetLocation) {
        this.targetground = targetground;
        this.targetLocation = targetLocation;
    }

    /**
     * Waters the ground when the actor has the required ability and stamina.
     *
     * @param actor actor performing the action
     * @param map current game map
     * @return a description of the watering result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
<<<<<<< Updated upstream
=======
        Location location = map.locationOf(actor);
>>>>>>> Stashed changes
        int cost = targetground.waterStamina();

        if (actor.hasAbility(ToolAbility.WATER) && actor.getStatistic(ActorStatistics.STAMINA) >= cost) {
            actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, cost);

            return targetground.water(actor, targetLocation);
        }
        return "Can't water " + targetground + "due to insufficent energy or dont have a Watering Can!";
    }

    /**
     * Returns the action text shown in the menu.
     *
     * @param actor actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " water "+ targetground.toString();
    }

}
