
package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.IndestructibleAbility;
import game.ability.TerrainAbility;

/**
 * Base class for weather-response structures.
 * Supports either one-time activation on placement
 * or automatic activation during each world turn.
 */
public abstract class WeatherResponseStructure extends Ground
{
    private final WeatherResponse response;

    protected WeatherResponseStructure(char symbol,String name, WeatherResponse response) {
        super(symbol, name);
        this.response = response;
        this.enableAbility(IndestructibleAbility.PROTECTED);
        this.enableAbility(TerrainAbility.FLOOD_RESISTANT);
    }
    @Override
    public final void tick(Location location) {
        if (!activatesOnPlacementOnly()) {
            response.respond(location);
        }
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Determines whether the response activates only on placement.
     *
     * @return true if the structure activates once when placed
     */
    public boolean activatesOnPlacementOnly() {
        return false;
    }

    /**
     * Activates the structure immediately after placement, if applicable.
     *
     * @param location the structure's location
     */
    public final void onPlaced(Location location) {
        if (activatesOnPlacementOnly()) {
            response.respond(location);
        }
    }
}

