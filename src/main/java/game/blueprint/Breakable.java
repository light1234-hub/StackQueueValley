package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents ground that can be broken with a pickaxe.
 */
public interface Breakable {
    /**
     * Breaks the object at a location.
     *
     * @param actor actor performing the break
     * @param location location containing the breakable object
     * @return a description of the break result
     */
    String breakBlock(Actor actor, Location location);

    /**
     * Returns the stamina required to break the object.
     *
     * @return break stamina cost
     */
    int breakStaminaCost();
}