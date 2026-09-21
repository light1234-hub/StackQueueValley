package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.farmeraction.HarvestAction;
import game.blueprint.Harvestable;
import game.item.TomatoItem;


/**
 * A tomato crop that can be harvested for tomato items.
 */
public class TomatoCrop extends Ground implements Harvestable {

    /**
     * Creates a tomato crop.
     */
    public TomatoCrop() {
        super('T',"Tomato crop");
    }

    @Override
    /**
     * Harvests the crop, replacing it with dirt and increasing maximum stamina.
     *
     * @param actor actor harvesting the crop
     * @param location crop location
     * @return a description of the harvest result
     */
    public String harvest(Actor actor, Location location){
        location.setGround(new Dirt());
        actor.getInventory().add(new TomatoItem());
        actor.getInventory().add(new TomatoItem());

        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE,1);

        return "Harvested Tomato!!";
    }

    @Override
    /**
     * Returns the stamina required to harvest the crop.
     *
     * @return stamina cost
     */
    public int staminaCost(){
        return 2;
    }

    @Override
    /**
     * Adds the harvest action for this crop.
     *
     * @param actor actor interacting with the crop
     * @param location crop location
     * @param direction direction from the actor to the crop
     * @return available actions
     */
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new HarvestAction(this,location));
        return actions;
    }
}