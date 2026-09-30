package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Attempts to teleport the actor to a chosen destination via the alien
 * device. 50% of the time it malfunctions instead, dropping the actor at a
 * random (enterable) location on whatever map they're currently on, rather
 * than the destination they picked.
 */
public class AlienDeviceAction extends Action {
    private final Location destination;
    private final Random random = new Random();

    public AlienDeviceAction(Location destination) {
        this.destination = destination;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (random.nextDouble() < 0.5) {
            GameMap currentMap = map.locationOf(actor).map();
            Location malfunctionLocation;
            int attempts = 0;
            do {
                int x = currentMap.getXRange().min()
                        + random.nextInt(currentMap.getXRange().max() - currentMap.getXRange().min() + 1);
                int y = currentMap.getYRange().min()
                        + random.nextInt(currentMap.getYRange().max() - currentMap.getYRange().min() + 1);
                malfunctionLocation = currentMap.at(x, y);
                attempts++;
            } while (!malfunctionLocation.canActorEnter(actor) && attempts < 20);

            if (malfunctionLocation.canActorEnter(actor)) {
                currentMap.moveActor(actor, malfunctionLocation);
            }
            return actor + "'s alien device malfunctions and drops them elsewhere on " + currentMap + "!";
        }
        map.moveActor(actor, destination);
        return actor + " uses the alien device to teleport to " + destination.map() + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses the alien device to travel to " + destination.map();
    }
}
