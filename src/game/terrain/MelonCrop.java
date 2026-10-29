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
import game.item.MelonItem;
import game.item.StrawberryItem;

import java.util.List;

public class MelonCrop extends Plant implements Harvestable, Waterable {

    private int timeWatered = 0;

    /**
     * Creates a Melon crop.
     */
    public MelonCrop(){
        super('Q',"Melon Crop");
    }

    @Override
    protected String takeTurn(Location location, int age) {
        Location child = spread(location, PlantGrowth.sprout(PlantGrowth.Species.MELON), age, 20);
        return child == null ? null : spreadMessage("Melon Sprout", child);
    }

    /**
     * Harvests the crop, with a 50% chance to either safely yield a Melon
     * or explode and damage all adjacent actors.
     *
     * @param actor actor harvesting the crop
     * @param location crop location
     * @return a description of the harvest result
     */
    @Override
    public String harvest(Actor actor, Location location){
        location.setGround(new Dirt());

        if(Math.random() >= 0.5){
            int damage = 20 - this.timeWatered;

            //actor stnading on the melon itself
            if(location.containsAnActor()){
                Actor victim = location.getActor();
                if (damage > 0){
                    victim.hurt(damage);
                }
            }

            List<Exit> exits = location.getExits();
            for(Exit exit:exits){
                Location plot = exit.getDestination();

                if(plot.containsAnActor()){
                    Actor victim = plot.getActor();

                    if(damage > 0){
                        victim.hurt(damage);
                    }
                }
            }
            return "The Melon exploded, damaging all nearby actors!";
        }
        actor.getInventory().add(new MelonItem());
        return "Melon has been harested and added into inventory";
    }

    /**
     * Returns the stamina required to harvest the crop.
     *
     * @return stamina cost
     */
    @Override
    public int staminaCost(){
        return 5;
    }

    /**
     * Waters the Melon.
     *50% chance of increase Max SP by 1
     * @param actor actor watering the crop
     * @param location crop location
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor,Location location){
        this.timeWatered ++;

        if(Math.random() >= 0.5){
            actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE,1);
            return "Watered Melon and MAX SP increase by 1 .";
        }

        return "Watered Melon.";
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

