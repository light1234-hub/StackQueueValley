package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.blueprint.Harvestable;

/**
 * Action that harvests a crop when the actor has sufficient stamina.
 */
public class HarvestAction extends Action {
    private final Harvestable crop;
    private final Location croploc;

    /**
     * Creates a harvest action for a crop.
     *
     * @param crop crop to harvest
     * @param croploc crop location
     */
    public HarvestAction(Harvestable crop,Location croploc){
        this.crop =crop;
        this.croploc = croploc;
    }

    /**
     * Harvests the crop and deducts its stamina cost.
     *
     * @param actor actor performing the action
     * @param map current game map
     * @return a description of the harvest result
     */
    @Override
    public String execute(Actor actor, GameMap map){

        if (actor.getStatistic(ActorStatistics.STAMINA) < crop.staminaCost()){
            return "You are too tired to harvest this crop";
        }

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE,crop.staminaCost());

        return crop.harvest(actor,croploc);
    }

    /**
     * Returns the action text shown in the menu.
     *
     * @param actor actor performing the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " harvest " + crop;
    }
}
