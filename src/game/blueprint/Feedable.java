package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An interface that is implemented by all items that can be fed
 */
public interface Feedable
{
    String feed(Actor actor, Location location);
}
