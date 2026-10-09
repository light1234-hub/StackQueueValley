package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.effect.FireEffect;

/**
 * Moves the actor to a fixed destination via a teleportation circle, then
 * sets every location adjacent to the destination on fire for 2 turns
 * (the arrival tile itself is spared, per the spec). Anyone who then stands
 * on one of those tiles while the fire is active catches a 3-turn, 5-damage
 * burn -- see FireEffect for how that's modelled.
 */
public class TeleportAction extends Action {
    private final Location destination;

    public TeleportAction(Location destination) {
        this.destination = destination;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        map.moveActor(actor, destination);
        for (Exit exit : destination.getExits()) {
            Location adjacent = exit.getDestination();
            adjacent.getGround().addStatus(new FireEffect(2, 0, 3, 5));
        }
        return actor + " steps through the teleportation circle and the ground around them catches fire!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses the teleportation circle";
    }
}
