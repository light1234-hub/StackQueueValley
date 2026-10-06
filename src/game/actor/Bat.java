package game.actor;

import edu.monash.fit2099.engine.positions.Location;
import game.item.StrawberryItem;

/** Mine creature with a fast attack that drops a fresh strawberry when struck. */
public class Bat extends HostileCreature {
    /** Creates a bat with 50 health points. */
    public Bat() { super("Bat", 'b', 50); }
    /** @return the bat's 35 attack damage */
    @Override protected int damage() { return 35; }
    /** @return the bat's 90 percent hit chance */
    @Override protected int hitChance() { return 90; }

    /** Drops a non-rotten strawberry on a randomly selected adjacent tile. */
    @Override public void onAttacked(Location location, boolean killed) {
        java.util.List<Location> nearby = location.getNearbyLocations(1);
        if (!nearby.isEmpty()) nearby.get(new java.util.Random().nextInt(nearby.size())).addItem(new StrawberryItem(false));
    }
}
