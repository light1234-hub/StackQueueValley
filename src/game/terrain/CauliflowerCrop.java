package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.farmeraction.HarvestAction;
import game.farmeraction.WaterAction;
import game.blueprint.Harvestable;
import game.blueprint.Waterable;
import game.item.CauliflowerItem;
import game.item.StrawberryItem;

import java.util.List;
import java.util.Random;

public class CauliflowerCrop extends Plant implements Harvestable, Waterable {

    private int timeWatered = 0;
    /**
     * Creates a Cauliflower crop.
     */
    public CauliflowerCrop() {
        super('Y',"Cauliflower Crop");
    }

    @Override
    protected String takeTurn(Location location, int age) { return null; }

    /**
     * Checks whether the cauliflower has been watered at least five times.
     *
     * @return true if ready to harvest
     */
    @Override
    public boolean canHarvest() {
        return timeWatered >= 5;
    }
    /**
     * Harvests the crop, replacing the plot with dirt and having a 50% chance
     * to drop a Cauliflower item.
     *
     * @param actor actor harvesting the crop
     * @param location crop location
     * @return a description of the harvest result
     */

    @Override
    public String harvest(Actor actor, Location location){
        location.setGround(new Dirt());
        if(Math.random()>=0.5) {
            actor.getInventory().add(new CauliflowerItem());
            return "Harvested the Cauliflower successfully!";
        }
        return "Harvested the Cauliflower, but it yielded nothing.";
    }

    /**
     * Waters the Cauliflower, with a 50% chance to turn adjacent standard ground into puddles.
     *
     * @param actor actor watering the crop
     * @param location crop location
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor,Location location){
        this.timeWatered ++;
        boolean puddleTriggered = false;
        if(Math.random()>=0.5) {
            puddleTriggered = true;
            List<Exit> exits = location.getExits();
            for(Exit exit:exits){
                Location plot = exit.getDestination();

                if(plot.getGround().hasAbility(TerrainAbility.CAN_BE_PUDDLE)){
                    plot.setGround(new Puddle());
                }
            }
        }
        if(puddleTriggered){
            return "Watered Cauliflower and adjacent ground turned into puddles!";
        }
        return "Watered Cauliflower";
    }


    /**
     * Returns the stamina required to water the crop.
     *
     * @return water stamina cost
     */
    @Override
    public int waterStamina(){
        return 2;
    }

    /**
     * Returns the stamina required to harvest the crop.
     *
     * @return stamina cost
     */
    @Override
    public int staminaCost(){
        return 3;
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
        actions.add(new HarvestAction(this, location));
        actions.add(new WaterAction(this,location));
        return actions;
    }
}
