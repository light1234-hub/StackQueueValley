package game.weatherblueprint;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Represent livestock that can be relocated
 */
public interface WeatherShelterable {
    /**
     * handles animal successful relocation
     * @param destination the animal's new location
     */
    void onRelocated(Location destination);
}
