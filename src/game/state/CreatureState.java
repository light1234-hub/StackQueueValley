package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actor.Zombie;

/**
 * Represents a state that a zombie can be in
 * Each state determines the zombie's next state
 * the action it performs during its turn
 * and any effect that occurs when entering the state
 */
public interface CreatureState {
    /**
     * Determine the next state of the Zombie based on the current game condition
     * @param zombie the Zombie whose state is being evaluated
     * @param map the current game map
     * @return the next CreatureState, or the current state if not transition occurs
     */
    CreatureState nextState(Zombie zombie, GameMap map);

    /**
     * Determines the action performed by the Zombie while in this state.
     * @param zombie the Zombie performing the action
     * @param map the current game map
     * @return the action to be performed by the Zombie
     */
    Action getAction(Zombie zombie,GameMap map);

    /**
     * Performs the one-time effect that occurs when the Zombie enters this state
     * @param zombie zombie the Zombie entering the state
     * @param map map the current game map
     */
    void onEnter(Zombie zombie, GameMap map);
}
