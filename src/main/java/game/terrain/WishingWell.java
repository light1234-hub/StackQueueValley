package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.WishAction;

/** Impassable well; the farmer wishes from an adjacent tile to travel to the linked well's side. */
public class WishingWell extends Ground {
    private final int cost;
    private Location destination;

    public WishingWell(int cost) {
        super('W', "Wishing Well");
        this.cost = cost;
    }

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