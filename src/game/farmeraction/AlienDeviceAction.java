package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;


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
            } while (!SafeArrival.isFree(malfunctionLocation, actor) && attempts < 20);

            if (SafeArrival.isFree(malfunctionLocation, actor)) {
                currentMap.moveActor(actor, malfunctionLocation);
            }
            return actor + "'s alien device malfunctions and drops them elsewhere on " + currentMap + "!";
        }
        Location arrival = SafeArrival.resolve(destination, actor);
        if (arrival == null) {
            return actor + "'s alien device fails: there is no free space at the destination.";
        }
        map.moveActor(actor, arrival);
        return actor + " uses the alien device to teleport to " + destination.map() + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses the alien device to travel to " + destination.map();
    }
}