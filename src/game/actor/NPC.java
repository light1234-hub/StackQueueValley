package game.actor;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Base class for non-player characters in the game.
 *
 * <p>An NPC is an actor controlled by the game rather than by the player. The
 * concrete NPC defines its turn behavior through {@link Actor#playTurn}.</p>
 */
public abstract class NPC extends Actor {

    /**
     * Creates a non-player character.
     *
     * @param name NPC name
     * @param displayChar character used to display the NPC on the map
     * @param hitPoints starting health points
     */
    protected NPC(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
    }
}
