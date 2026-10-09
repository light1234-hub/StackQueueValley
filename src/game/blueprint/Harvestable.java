package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a crop or ground object that can be harvested.
 */
public interface Harvestable {
    /**
     * Harvests the object at a location.
     *
     * @param actor actor performing the harvest
     * @param location location containing the harvestable object
     * @return a description of the harvest result
     */
    String harvest (Actor actor, Location location);

    /**
     * Returns the stamina required to harvest the object.
     *
     * @return harvest stamina cost
     */
    int staminaCost();

    /**
     * Checks whether this harvestable object is currently ready to harvest.
     *
     * @return true if it can be harvested
     */
    default boolean canHarvest() {
        return true;
    }
}
