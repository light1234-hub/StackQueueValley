package game.item;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.AlienDeviceAction;

import java.util.ArrayList;
import java.util.List;

/**
 * A carried device offering teleportation to one of several fixed
 * destinations, chosen before the game starts. Each use carries a 50% chance
 * of malfunctioning and dropping the farmer at a random location on the map
 * they are currently on, instead of the destination they picked.
 * <p>
 * Call {@link #addDestination(Location)} once per destination while setting
 * up the world, before the game runs.
 */
public class AlienDevice extends Item {
    private final List<Location> destinations = new ArrayList<>();

    public AlienDevice() {
        super("Alien device", 'A');
        this.makePortable();
    }

    /**
     * Registers a fixed destination this device can teleport the farmer to.
     *
     * @param destination the location to add as a possible destination
     */
    public void addDestination(Location destination) {
        destinations.add(destination);
    }

    /**
     * Offers one teleport option per registered destination.
     *
     * @param owner the actor carrying this device
     * @param map the map the actor is currently on
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        for (Location destination : destinations) {
            actions.add(new AlienDeviceAction(destination));
        }
        return actions;
    }
}
