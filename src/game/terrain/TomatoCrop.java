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
 * Mature tomato crop that produces tomatoes every seven turns and has a 10%
 * chance to self-replicate every twenty turns. It can also be harvested.
 */
public class TomatoCrop extends Plant implements Harvestable {

    /**
     * Creates a tomato crop.
     */
    public TomatoCrop() {
        super('T',"Tomato Crop");
    }

    /** Attempts tomato production first, then 10% self-replication every twentieth turn.
     * @param location location occupied by this crop
     * @param age current crop age in turns
     * @return message for the first successful action, or {@code null} if none occurs
     */
    @Override
    protected String takeTurn(Location location, int age) {
        if (due(age, 7)) {
            String result = produce(location, "Tomato", TomatoItem::new);
            if (result != null) return result;
        }
        if (due(age, 20) && chance(0.10)) {
            Location child = spread(location, new TomatoCrop(), age, 20);
            if (child != null) return spreadMessage("Tomato Crop", child);
        }
        return null;
    }

    /**
     * Harvests the crop, replacing it with dirt and increasing maximum stamina.
     *
     * @param actor actor harvesting the crop
     * @param location crop location
     * @return a description of the harvest result
     */
    @Override
    public String harvest(Actor actor, Location location){
        location.setGround(new Dirt());
        actor.getInventory().add(new TomatoItem());
        actor.getInventory().add(new TomatoItem());

        actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE,1);

        return "Harvested Tomato!!";
    }

    /**
     * Returns the stamina required to harvest the crop.
     *
     * @return stamina cost
     */
    @Override
    public int staminaCost(){
        return 2;
    }

    /**
     * Adds the harvest action for this crop.
     *
     * @param actor actor interacting with the crop
     * @param location crop location
     * @param direction direction from the actor to the crop
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new HarvestAction(this,location));
        return actions;
    }
}
