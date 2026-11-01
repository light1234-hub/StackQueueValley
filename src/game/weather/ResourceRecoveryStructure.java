
package game.weather;

/**
 * Automatically recovers resources from weather-damaged terrain.
 */
public class ResourceRecoveryStructure extends WeatherResponseStructure {

    public ResourceRecoveryStructure() {
        super('R', "Resource Recovery Structure", new ResourceRecoveryResponse());
    }
}

