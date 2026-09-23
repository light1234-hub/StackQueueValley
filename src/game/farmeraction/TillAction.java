package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.ToolAbility;
import game.blueprint.Tillable;

/**
 * Action that tills ground using a hoe and stamina.
 */
public class TillAction extends Action {
    private Tillable targetground;

    /**
     * Creates a tilling action for ground.
     *
     * @param targetground ground to till
     */
    public TillAction(Tillable targetground){
        this.targetground = targetground;
    }

    /**
     * Tills the ground when the actor has the required ability and stamina.
     *
     * @param actor actor performing the action
     * @param map current game map
     * @return a description of the tilling result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        Tillable ground = targetground;
        int cost = targetground.tillstamina();
        if (actor.hasAbility(ToolAbility.TILL) && actor.getStatistic(ActorStatistics.STAMINA) >= cost) {
            actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, cost);

            return ground.till(location);
        }
        return "You are either too tired to till dirt or don't have a Hoe to till dirt.";
    }

    /**
     * Returns the action text shown in the menu.
     *
     * @param actor actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " tills "+ targetground.toString();
    }

}
