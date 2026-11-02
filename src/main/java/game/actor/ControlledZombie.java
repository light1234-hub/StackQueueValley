package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A Zombie that obeys stun, lure and tamed behaviours. The existing Zombie ignores behaviours
 * because it overrides playTurn, and it cannot be modified, so CreatureControl swaps a plain
 * Zombie for this subclass (same position, same health) the first time it is affected.
 * When no control behaviour acts, it plays exactly like a normal Zombie.
 */
public class ControlledZombie extends Zombie implements BehaviourHost {
    private final List<Behaviour<Actor, Action>> controls = new ArrayList<>();

    @Override
    public void addControl(Behaviour<Actor, Action> behaviour) {
        controls.add(0, behaviour); // newest first, like addPriorityBehaviour
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (isConscious()) {
            for (Behaviour<Actor, Action> control : controls) {
                Action action = control.operate(this, map.locationOf(this));
                if (action != null) {
                    return action;
                }
            }
        }
        return super.playTurn(actions, lastAction, map, display);
    }
}