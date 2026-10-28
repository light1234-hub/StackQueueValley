package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.WishAction;

/**
 * A wishing well linked to a well on another map. The farm well charges 100
 * coins; the return well in Alchemy Valley is created with a cost of 0.
 * Like a door, it is impassable and used from an adjacent tile.
 * <p>
 * Link both wells with {@link #setDestination(Location)} after both maps exist.
 */
public class WishingWell extends Ground {
    private final int cost;
    private Location destination;

    /** @param cost coins charged per wish (0 for free) */
    public WishingWell(int cost) {
        super('W', "Wishing Well");
        this.cost = cost;
    }

    /** @param destination the location of the well on the other map */
    public void setDestination(Location destination) {
        this.destination = destination;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (destination != null) {
            actions.add(new WishAction(destination, cost, direction));
        }
        return actions;
    }
}