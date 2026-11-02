package game.weather;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Defines a response to environmental conditons
 * Implementation can modify nearby terrain, relocate animals, or recover resources
 */
public interface WeatherResponse {
    /**
     * Applies an environmental response around a location
     * @param location the centre of the response effects
     */
    void respond(Location location);
}
