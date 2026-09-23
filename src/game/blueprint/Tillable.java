package game.blueprint;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents ground that can be tilled.
 */
public interface Tillable {
    /**
     * Tills the ground at the supplied location.
     *
     * @param location location containing the ground
     * @return a description of the tilling result
     */
    String till (Location location);
    int tillstamina();
}
