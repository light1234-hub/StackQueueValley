package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an item that can be used to feed an animal.
 */
public interface AnimalFood {
    /**
     * Applies the food's effect to an actor.
     *
     * @param actor actor using the food to feed an animal
     * @return a description of the applied effect
     */
    String foodEffect(Actor actor);
}
