package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.HarvestAction;
import game.farmeraction.WaterAction;
import game.blueprint.Harvestable;
import game.blueprint.Waterable;
import game.item.StrawberryItem;

import java.util.List;
import java.util.Random;


/** A mature strawberry crop that produces fruit and supports harvesting and watering. */
public class StrawberryCrop extends Plant implements Harvestable, Waterable {
    /** Number of times this crop has been watered; affects harvest destruction chance. */
    private int timeWatered = 0;
    /** Random source used to choose adjacent plots for the watering effect. */
    private Random random = new Random();
    /**
     * Creates a strawberry crop.
     */
    public StrawberryCrop() {
        super('S',"Strawberry Crop");
    }

    /** Produces one strawberry every fifth turn.
     * @param location location occupied by this crop
     * @param age current crop age in turns
     * @return produce message when due, otherwise {@code null}
     */
    @Override
    protected String takeTurn(Location location, int age) {
        if (due(age, 5)) return produce(location, "Strawberry", StrawberryItem::new);
        return null;
    }

    /**
     * Harvests the crop, collecting two strawberries and potentially replacing
     * the plot with dirt depending on the crop's watered state.
     *
     * @param actor actor harvesting the crop
     * @param location crop location
     * @return a description of the harvest result
     */

    @Override
    public String harvest(Actor actor, Location location){
        actor.getInventory().add(new StrawberryItem());
        actor.getInventory().add(new StrawberryItem());

        double destructionChance =Math.max(0, 50.0 - timeWatered) / 100.0;

        if(Math.random() < destructionChance){
            location.setGround(new Dirt());
            return "Harvested two Strawberries, but the crop was destroyed!";
        }
        return "Harvested Two Strawberry!!";
    }

    /**
     * Waters the strawberry and drops two strawberry items on adjacent tiles.
     *
     * @param actor actor watering the crop
     * @param location crop location
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor,Location location){
        this.timeWatered ++;

        List<Exit>exits = location.getExits();

        for(int i = 0; i<2; i++){
            int randomidx = random.nextInt(exits.size());
            Location plot = exits.get(randomidx).getDestination();
            plot.addItem(new StrawberryItem());
        }

        return "Watered Strawberry and added two Strawberry.";
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
     * Returns the stamina required to water the crop.
     *
     * @return water stamina cost
     */
    @Override
    public int waterStamina(){
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
