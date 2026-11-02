package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.IndestructibleAbility;
import game.farmeraction.TeleportAction;

/**
 * A teleportation circle linking a fixed location on one map to a fixed
 * location on another. Like Ladder, a pair of these must be linked with
 * setDestination() once both maps exist. Arriving sets the ground on fire
 * around the destination -- see TeleportAction.
 */
public class TeleportationCircle extends Ground
{
    private Location destination;

    public TeleportationCircle() {
        super('O', "Teleportation circle");
        this.enableAbility(IndestructibleAbility.PROTECTED);
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (direction.isEmpty() && destination != null) {
            actions.add(new TeleportAction(destination));
        }
        return actions;
    }
}
