package game.actor;

import edu.monash.fit2099.engine.positions.Location;

/** Mine creature that explodes for 50 damage around itself when killed. */
public class CaveGhost extends HostileCreature {
    /** Creates a cave ghost with 250 health points. */
    public CaveGhost() { super("Cave ghost", 'G', 250); }
    /** @return the ghost's 25 attack damage */
    @Override protected int damage() { return 25; }
    /** @return the ghost's 75 percent hit chance */
    @Override protected int hitChance() { return 75; }

    /** Damages every adjacent actor when a hit kills the ghost. */
    @Override public void onAttacked(Location location, boolean killed) {
        if (!killed) return;
        for (edu.monash.fit2099.engine.positions.Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) adjacent.getActor().hurt(50);
        }
    }
}
