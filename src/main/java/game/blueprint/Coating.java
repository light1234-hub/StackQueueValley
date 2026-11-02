package game.blueprint;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An attack effect that can be applied to a {@link Coatable} object.
 *
 * <p>Each coating encapsulates its own attack behavior. Adding another coating
 * can therefore be done with another implementation instead of adding another
 * branch to the sword attack action.</p>
 */
public interface Coating {
    /**
     * Applies this effect for an attack. This is invoked for both hits and
     * misses, as required by the coating rules.
     *
     * @param attacker actor making the attack
     * @param target actor the attack was aimed at
     * @param map map on which the attack occurs
     * @return a description of the coating effect
     */
    String apply(Actor attacker, Actor target, GameMap map);

    /** @return the display name of this coating */
    String getName();
}
