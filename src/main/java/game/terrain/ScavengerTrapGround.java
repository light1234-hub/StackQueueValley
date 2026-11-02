package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A temporary snare created by scavengers. The trap blocks entry for three turns
 * and then restores the ground that was underneath it.
 */
public class ScavengerTrapGround extends Ground {
    private static final int DURATION_TURNS = 3;

    private final Ground underlyingGround;
    private int turnsRemaining = DURATION_TURNS;

    /**
     * Creates a trap that remembers which ground should be restored afterward.
     *
     * @param underlyingGround ground to restore when the trap expires
     * @throws IllegalArgumentException if no underlying ground is supplied
     */
    public ScavengerTrapGround(Ground underlyingGround) {
        super('^', "Scavenger Snare");
        if (underlyingGround == null) {
            throw new IllegalArgumentException("A trap must remember the ground beneath it");
        }
        this.underlyingGround = underlyingGround;
    }

    /**
     * Prevents actors from entering a tile while the snare is active.
     *
     * @param actor actor attempting to enter
     * @return always {@code false}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Counts down the snare lifetime and restores the original terrain on expiry.
     *
     * @param location location occupied by this trap
     */
    @Override
    public void tick(Location location) {
        turnsRemaining--;
        if (turnsRemaining <= 0 && location.getGround() == this) {
            location.setGround(underlyingGround);
        }
    }
}