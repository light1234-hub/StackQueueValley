package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.TravelAction;

/**
 * A ladder connecting a fixed location on one map to a fixed location on
 * another (e.g. one tile on the farm to one tile in the mines). Two Ladder
 * instances must be paired up with setDestination() once both maps have
 * been built -- see the world setup example.
 * <p>
 * Climbing carries a 50% chance of losing footing and falling for 25
 * damage; the fall never prevents arrival at the destination.
 */
public class Ladder extends Ground {
    private Location destination;

    public Ladder() {
        super('H', "Ladder");
    }

    /**
     * Links this ladder to the location of its corresponding ladder on the
     * other map. Must be called once, after both maps exist.
     *
     * @param destination the location this ladder leads to
     */
    public void setDestination(Location destination) {
        this.destination = destination;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (direction.isEmpty() && destination != null) {
            actions.add(new TravelAction(destination, "the ladder", 0.5, 25));
        }
        return actions;
    }
}
