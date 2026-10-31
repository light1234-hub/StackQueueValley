
package game.weather;

import game.ability.TerrainAbility;

/**
 * Automatically relocates endangered livestock.
 */
public class WildlifeShelter extends WeatherResponseStructure {

    public WildlifeShelter() {
        super('H', "Wildlife Shelter", new WildlifeResponse());
        this.hasAbility(TerrainAbility.SHELTER);
    }
}

