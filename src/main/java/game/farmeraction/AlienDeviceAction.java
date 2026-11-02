package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
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
            return malfunction(actor, map);
        }

        if (map.locationOf(actor) == destination) {
            return actor + " is already at that destination.";
        }
        // The destination is fixed: if someone is standing there, the teleport simply fails.
        if (destination.containsAnActor() || !destination.canActorEnter(actor)) {
            return actor + "'s alien device cannot teleport: the destination is occupied.";
        }
        map.moveActor(actor, destination);
        return actor + " uses the alien device to teleport to " + destination.map() + ".";
    }

    /**
     * The device malfunctions: the actor is dropped on a random free tile of the map
     * they are currently on. Every free tile is collected first and one is picked,
     * instead of guessing random coordinates and hoping one is free.
     */
    private String malfunction(Actor actor, GameMap map) {
        GameMap currentMap = map.locationOf(actor).map();
        List<Location> landingSpots = new ArrayList<>();
        for (int x = currentMap.getXRange().min(); x <= currentMap.getXRange().max(); x++) {
            for (int y = currentMap.getYRange().min(); y <= currentMap.getYRange().max(); y++) {
                Location candidate = currentMap.at(x, y);
                if (candidate.canActorEnter(actor) && !candidate.containsAnActor()) {
                    landingSpots.add(candidate);
                }
            }
        }

        if (landingSpots.isEmpty()) {
            return actor + "'s alien device malfunctions, but there is nowhere free to land, so they stay put.";
        }
        Location landing = landingSpots.get(random.nextInt(landingSpots.size()));
        currentMap.moveActor(actor, landing);
        return actor + "'s alien device malfunctions and drops them elsewhere on " + currentMap + "!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses the alien device to travel to " + destination.map();
    }
}