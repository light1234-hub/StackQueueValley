
package game.weather;

/**
 * Automatically drains nearby flooded terrain.
 */
public class DrainageStructure extends WeatherResponseStructure {

    public DrainageStructure() {
        super('D', "Drainage Structure", new DrainageResponse());
    }

    @Override
    public boolean activatesOnPlacementOnly() {
        return true;
    }
}

