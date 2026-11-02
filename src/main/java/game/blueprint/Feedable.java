package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents an actor that can be fed at a location.
 */
public interface Feedable {
    /**
     * Feeds the object and applies its feeding effect.
     *
     * @param actor actor performing the feeding
     * @param location location where the feeding occurs
     * @return a description of the feeding result
     */
    String feed(Actor actor, Location location);
}
