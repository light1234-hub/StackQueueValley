package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.ability.TerrainAbility;
import game.blueprint.Corruptible;
import game.blueprint.Tillable;
import game.blueprint.Waterable;
import game.farmeraction.TillAction;
import game.farmeraction.WaterAction;

/**
 * Ordinary dirt used as unt tilled farm terrain.
 * Dirt can be tilled or watered and provides capabilities for several dynamic
 * REQ3 environmental effects.
 */
public class Dirt extends Ground implements Tillable, Waterable, Corruptible {

    /** Creates dirt and enables the interactions supported by ordinary ground. */
    public Dirt() {
        super('.', "Dirt");
        enableAbility(TerrainAbility.CAN_BE_PUDDLE);
        enableAbility(TerrainAbility.CAN_BE_CORRUPTED);
        enableAbility(GroundAbility.GROVE_ENRICHABLE);
        enableAbility(GroundAbility.BLIGHT_INFECTABLE);
        enableAbility(GroundAbility.TRAP_BUILDABLE);
        enableAbility(GroundAbility.COLLAPSE_REPLACEABLE);
        enableAbility(GroundAbility.QUEST_FEATURE_PLACEABLE);
        enableAbility(GroundAbility.RESOURCE_REVEALABLE);
    }

    /**
     * Converts this dirt into soil.
     *
     * @param location location containing this dirt
     * @return a description of the tilling result
     */
    @Override
    public String till(Location location) {
        location.setGround(new Soil());
        return super.toString() + " turns to soil!";
    }

    /**
     * Converts this dirt into a puddle.
     *
     * @param actor actor watering this dirt
     * @param location location containing this dirt
     * @return a description of the watering result
     */
    @Override
    public String water(Actor actor, Location location) {
        location.setGround(new Puddle());
        return super.toString() + " turns to a puddle!";
    }

    /**
     * Returns the stamina required to till dirt.
     *
     * @return stamina cost for tilling
     */
    @Override
    public int tillstamina() {
        return 2;
    }

    /**
     * Returns the stamina required to water dirt.
     *
     * @return stamina cost for watering
     */
    @Override
    public int waterStamina() {
        return 2;
    }

    /**
     * Replaces this dirt with corrupted terrain.
     *
     * @param location location containing this dirt
     */
    @Override
    public void corrupt(Location location) {
        location.setGround(new CorruptedDirt());
    }

    /**
     * Provides tilling and watering actions when an actor is adjacent to this dirt.
     *
     * @param actor actor interacting with this ground
     * @param location location containing this ground
     * @param direction direction from the actor to this ground
     * @return the actions available to the actor
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (direction.isEmpty()) {
            actions.add(new TillAction(this));
            actions.add(new WaterAction(this, location));
        }
        return actions;
    }
}