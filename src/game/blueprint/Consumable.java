package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an object that an actor can consume.
 */
public interface Consumable {
    /**
     * Applies the consumable's effect to an actor.
     *
     * @param actor actor consuming the object
     * @return a description of the consumption result
     */
    String consume(Actor actor);
}

