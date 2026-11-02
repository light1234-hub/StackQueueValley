package game.weatherblueprint;

import edu.monash.fit2099.engine.positions.Location;
/**
 * Represents weather-damaged terrain that can
 * be transformed into recoverable resources.
 */
public interface WeatherRecoverable {
    /**
     * Attempts to recover resources from this terrain.
     *
     * @param location location containing the terrain
     * @return true if resources were recovered
     */
    boolean recover(Location location);
}
