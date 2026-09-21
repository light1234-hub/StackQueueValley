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
    private Waterable targetground;

    /**
     * Creates a watering action for ground.
     *
     * @param targetground ground to water
     */
    public WaterAction(Waterable targetground){
        this.targetground=targetground;
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
        Location location = map.locationOf(actor);
        Waterable ground = targetground;

        if (actor.hasAbility(ToolAbility.WATER) && actor.getStatistic(ActorStatistics.STAMINA) >= 2) {
            actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, 2);

            return ground.water(actor,location);
        }
        return "Can't water " + targetground.toString() + "due to insufficent energy or dont have a Watering Can!";
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
