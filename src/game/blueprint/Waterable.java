package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents ground that can be watered.
 */
public interface Waterable {
    /**
     * Waters the ground at the supplied location.
     *
     * @param location location containing the ground
     * @return a description of the watering result
     */
    String water(Actor actor,Location location);
    int waterStamina();
}
