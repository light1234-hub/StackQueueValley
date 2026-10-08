package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.effect.FireEffect;


public class TeleportAction extends Action {
    private final Location destination;

    public TeleportAction(Location destination) {
        this.destination = destination;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location arrival = SafeArrival.resolve(destination, actor);
        if (arrival == null) {
            return actor + " cannot use the teleportation circle: there is no free space at the other end.";
        }
        map.moveActor(actor, arrival);
        for (Exit exit : arrival.getExits()) {
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

