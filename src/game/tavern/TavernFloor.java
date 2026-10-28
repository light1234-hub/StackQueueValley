package game.tavern;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/**
 * Represents the floor of the Tavern.
 * Allows actors to walk on it without providing farming interactions.
 */
public class TavernFloor extends Ground {

    /**
     * Creates a Tavern floor tile.
     */
    public TavernFloor() {
        super('-', "Tavern Floor");
    }

    /**
     * Determines whether an actor can enter the floor.
     *
     * @param actor the actor attempting to enter
     * @return true because the floor is walkable
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}
