package game.actor;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;

/**
 * Shared inventory transfers for resource-handling NPCs. Centralising ground
 * pickup and inventory drops keeps survivor and scavenger rules consistent.
 */
public class PortableResourceTransfer {
    private PortableResourceTransfer() { }

    /** Collects portable items from the supplied tile into the actor's inventory. */
    public static void collect(Actor actor, Location location) {
        for (Item item : new ArrayList<>(location.getItems())) {
            if (item.hasAbility(ItemAbility.PORTABLE)) {
                location.removeItem(item);
                actor.getInventory().add(item);
            }
        }
    }

    /** Drops all carried items on the supplied tile, leaving inventory empty. */
    public static void dropAll(Actor actor, Location location) {
        for (Item item : new ArrayList<>(actor.getInventory().getItems())) {
            actor.getInventory().remove(item);
            location.addItem(item);
        }
    }
}
