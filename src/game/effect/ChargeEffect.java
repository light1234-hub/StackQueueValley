package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An electrical charge sitting on a piece of ground (e.g. a Puddle), released
 * by breaking a copper node. Damages any actor standing on the charged
 * ground every turn for as long as the charge is active.
 * <p>
 * This status is attached to the Ground itself (Ground extends GameEntity,
 * so it can hold statuses the same way an Actor can), not to an actor --
 * that's what lets it keep damaging whoever happens to be standing there.
 */
public class ChargeEffect implements Status {
    private int duration;

    /**
     * Creates a charge effect.
     *
     * @param duration number of turns the charge remains active. The
     *                 requirement spec doesn't give an exact number of turns
     *                 for how long charged ground stays charged, so this is
     *                 set generously long by default -- adjust the value
     *                 passed in by Puddle.charge() if your spec expects a
     *                 specific duration.
     */
    public ChargeEffect(int duration) {
        this.duration = duration;
    }

    /**
     * Deals 20 damage to whoever is standing on the charged ground this
     * turn, then ticks down the remaining duration.
     *
     * @param currEntity the ground this charge is attached to
     * @param location the location of that ground
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (duration > 0) {
            if (location.containsAnActor()) {
                location.getActor().hurt(20);
            }
            duration--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return duration > 0;
    }
}
