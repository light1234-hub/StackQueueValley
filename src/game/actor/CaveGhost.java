package game.actor;

import edu.monash.fit2099.engine.positions.Location;

public class CaveGhost extends HostileCreature {
    public CaveGhost() { super("Cave ghost", 'G', 250); }
    @Override protected int damage() { return 25; }
    @Override protected int hitChance() { return 75; }
    @Override public void onAttacked(Location location, boolean killed) {
        if (!killed) return;
        for (edu.monash.fit2099.engine.positions.Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) adjacent.getActor().hurt(50);
        }
    }
}
