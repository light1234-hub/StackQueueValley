package game.blueprint;

import edu.monash.fit2099.engine.positions.Location;

/** Capability for actors that react after taking an attack. */
public interface AttackReactive {
    /**
     * Applies the actor's response to an attack.
     *
     * @param location actor's location when attacked
     * @param killed whether the attack made the actor unconscious
     */
    void onAttacked(Location location, boolean killed);
}
