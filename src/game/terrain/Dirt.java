package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.farmeraction.TillAction;
import game.farmeraction.WaterAction;
import game.blueprint.Corruptible;
import game.blueprint.Tillable;
import game.blueprint.Waterable;


public class Dirt extends Ground implements Tillable, Waterable, Corruptible {

    /**
     * A class representing dirt on the ground.
     */
    public Dirt() {
        super('.', "Dirt");
        this.enableAbility(TerrainAbility.CAN_BE_PUDDLE);
        this.enableAbility(TerrainAbility.CAN_BE_CORRUPTED);
    }

    /**
     * Converts this dirt into soil.
     *
     * @param location location containing the dirt
     * @return a description of the tilling result
     */
    @Override
    public String till(Location location){

        location.setGround(new Soil());
        return super.toString() + " turn to soil!";
    }

    /**
     * Converts this dirt into a puddle.
     *
     * @param location location containing the dirt
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor,Location location){
        location.setGround(new Puddle());
        return super.toString()+ " turn to puddle!";
    }

    @Override
    public int tillstamina(){return 2;}

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
     * Corrupts this dirt by replacing it with corrupted dirt.
     *
     * @param location location containing the dirt
     */
    @Override
    public void corrupt(Location location){
        location.setGround(new CorruptedDirt());
    }

    /**
     * Provides tilling and watering actions when the dirt is adjacent to the actor.
     *
     * @param actor actor interacting with the dirt
     * @param location dirt location
     * @param direction direction from the actor to the dirt
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction){
        ActionList actions = super.allowableActions(actor,location,direction);
        if (direction.isEmpty()){
            actions.add(new TillAction(this));
            actions.add(new WaterAction(this,location));
        }

        return actions;
    }
}