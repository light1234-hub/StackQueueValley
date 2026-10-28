
package game.weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A weather structure that activates its response
 * automatically on each world turn.
 */
public abstract class WeatherResponseStructure extends Ground {
    private final WeatherResponse response;

    protected WeatherResponseStructure(char symbol,String name, WeatherResponse response) {
        super(symbol, name);
        this.response = response;
    }
    @Override
    public final void tick(Location location) {
        response.respond(location);
    }
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}

