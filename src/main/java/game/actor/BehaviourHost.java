package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;

/** A creature that accepts high-priority control behaviours (stun / lure / tamed). */
public interface BehaviourHost {
    void addControl(Behaviour<Actor, Action> behaviour);
}