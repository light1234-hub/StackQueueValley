package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.farmeraction.HarvestAction;
import game.farmeraction.WaterAction;
import game.blueprint.Harvestable;
import game.blueprint.Waterable;
import game.item.ParsnipItem;

import java.util.Random;

import java.util.List;

/**
 * Mature parsnip crop that produces a parsnip every fifteen turns and supports
 * the existing watering and harvesting interactions.
 */
public class ParsnipCrop extends Plant implements Harvestable, Waterable {
    /** Random source used when harvesting may drop a parsnip on an adjacent tile. */
    private Random random = new Random();
    /** Number of times this crop has been watered, affecting its destruction chance. */
    private int timeWatered = 0;

    /**
     * Creates a parsnip crop.
     */
    public ParsnipCrop() {
        super('P',"Parsnip Crop");
    }

    /** Produces one parsnip every fifteenth turn.
     * @param location location occupied by this crop
     * @param age current crop age in turns
     * @return produce message when due, otherwise {@code null}
     */
    @Override
    protected String takeTurn(Location location, int age) {
        if (due(age, 15)) return produce(location, "Parsnip", ParsnipItem::new);
        return null;
    }

    /**
     * Harvests the crop, replacing it with dirt and possibly producing an
     * additional parsnip on a neighbouring plot.
     *
     * @param actor actor harvesting the crop
     * @param location crop location
     * @return a description of the harvest result
     */
    @Override
    public String harvest(Actor actor, Location location){
        location.setGround(new Dirt());
        actor.getInventory().add(new ParsnipItem());

        if(Math.random()<=0.5){
            List<Exit>exits = location.getExits();

            int randomidx = random.nextInt(exits.size());
            Location plot = exits.get(randomidx).getDestination();

            plot.addItem(new ParsnipItem());
        }
        return "Harvested parsnip!!";
    }


    /**
     * Waters the parsnip, healing the actor and risking crop destruction.
     *
     * @param actor actor watering the crop
     * @param location crop location
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor,Location location){
        this.timeWatered ++;
        actor.heal(20);

        double destructionChance =(10.0 + timeWatered)/100.0;

        if(Math.random() < destructionChance){
            location.setGround(new Dirt());
            return "Watered Parsnip, but the crop was destroyed";
        }
        return "Watered Parsnip and healed 20 HP!";

    }


    /**
     * Returns the stamina required to water the crop.
     *
     * @return water stamina cost
     */
    @Override
    public int waterStamina(){
        return 1;
    }

    /**
     * Returns the stamina required to harvest the crop.
     *
     * @return stamina cost
     */
    @Override
    public int staminaCost(){
        return 1;
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
        actions.add(new WaterAction(this,location));
        return actions;
    }
}
