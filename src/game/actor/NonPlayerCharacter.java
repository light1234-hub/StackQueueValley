package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Common turn handling for non-player characters. Behaviours are evaluated in
 * priority order; the first behaviour returning an action wins. A behaviour
 * returning null permits lower-priority behaviours to run on the same turn.
 * An NPC without an applicable behaviour waits rather than duplicating this
 * logic in each concrete actor.
 */
public abstract class NonPlayerCharacter extends Actor {
    private final List<Behaviour<Actor, Action>> behaviours = new ArrayList<>();

    /** Creates an NPC with the engine's ordinary health and display values. */
    protected NonPlayerCharacter(String name, char glyph, int hitPoints) {
        super(name, glyph, hitPoints);
    }

    /** Adds a reusable decision at the end of the priority order. */
    protected final void addBehaviour(Behaviour<Actor, Action> behaviour) {
        behaviours.add(Objects.requireNonNull(behaviour));
    }

    /** Inserts a decision ahead of those registered by a superclass. */
    protected final void addPriorityBehaviour(Behaviour<Actor, Action> behaviour) {
        behaviours.add(0, Objects.requireNonNull(behaviour));
    }

    /** Evaluates behaviours, taking the first available action. */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            unconscious(map);
            return new DoNothingAction();
        }
        Location location = map.locationOf(this);
        for (Behaviour<Actor, Action> behaviour : behaviours) {
            Action next = behaviour.operate(this, location);
            if (next != null) {
                return next;
            }
        }
        return new DoNothingAction();
    }
}
