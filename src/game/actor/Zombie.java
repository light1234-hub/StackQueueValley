package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.state.CreatureState;
import game.state.HealingState;

/**
 * Temporary Zombie implementation used to test the state system.
 */
public class Zombie extends HostileCreature {

    private CreatureState currentState;
    private boolean stateEntered = false;

    /**
     * Creates a Zombie starting in the healing state.
     */
    public Zombie() {
        super("Zombie", 'Z', 100);
        this.currentState = new HealingState();
    }

    /**
     * Performs the Zombie's turn according to its current state.
     *
     * @param actions available actions
     * @param lastAction previous action
     * @param map current game map
     * @param display game display
     * @return action performed by the Zombie
     */
    @Override
    public Action playTurn(
            ActionList actions,
            Action lastAction,
            GameMap map,
            Display display) {

        // Run onEnter once for the initial state.
        if (!stateEntered) {
            currentState.onEnter(this, map);
            stateEntered = true;
        }

        CreatureState nextState = currentState.nextState(this, map);

        if (nextState != currentState) {
            currentState = nextState;
            currentState.onEnter(this, map);
        }

        return currentState.getAction(this, map);
    }

    /**
     * Temporary attack damage.
     *
     * @return attack damage
     */
    @Override
    protected int damage() {
        return 20;
    }

    /**
     * Temporary attack hit chance.
     *
     * @return hit chance percentage
     */
    @Override
    protected int hitChance() {
        return 75;
    }

    /**
     * Returns the Zombie's current state.
     *
     * @return current state
     */
    public CreatureState getCurrentState() {
        return currentState;
    }
}
