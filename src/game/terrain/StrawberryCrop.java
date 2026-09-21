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


public class StrawberryCrop extends Ground implements Harvestable, Waterable {
    private int timeWatered = 0;
    private Random random = new Random();
    /**
     * Creates a strawberry crop.
     */
    public StrawberryCrop() {
        super('S',"Strawberry crop");
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

        double destructionChance =(50.0 - timeWatered)/100.0;

        if(Math.random() < destructionChance){
            location.setGround(new Dirt());
            return "Harvested two Strawberries, but the crop was destroyed!";
        }
        return "Harvested Two Strawberry!!";
    }

    /**
     * Waters the strawberry, healing the actor and risking crop destruction.
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
        actions.add(new WaterAction(this));
        return actions;
    }
}
