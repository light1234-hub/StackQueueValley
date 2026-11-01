package game.weatherblueprint;

import edu.monash.fit2099.engine.positions.Location;


/**
 * Represents terrain that can be drained
 * Classes implementing this interface define how they respond when drainage occurs
 */
public interface Drainable {
    /** Attempts to drain this terrain
     * @param location location containing the terrain
     * @return true if drainage was successful otherwise false
     */
    boolean drain(Location location);
}
