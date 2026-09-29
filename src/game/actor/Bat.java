package game.actor;

import edu.monash.fit2099.engine.positions.Location;
import game.item.StrawberryItem;

public class Bat extends HostileCreature {
    public Bat() { super("Bat", 'b', 50); }
    @Override protected int damage() { return 35; }
    @Override protected int hitChance() { return 90; }
    @Override public void onAttacked(Location location, boolean killed) {
        java.util.List<Location> nearby = location.getNearbyLocations(1);
        if (!nearby.isEmpty()) nearby.get(new java.util.Random().nextInt(nearby.size())).addItem(new StrawberryItem(false));
    }
}
