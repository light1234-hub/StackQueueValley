
package game.weather;

import game.ability.TerrainAbility;

/**
 * Automatically relocates endangered livestock.
 */
public class Shelter extends WeatherResponseStructure {

    public Shelter() {
        super('H', "Wildlife Shelter", new ShelterResponse());
        this.enableAbility(TerrainAbility.SHELTER);
    }
}

