package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * A generic action for moving an actor to a fixed Location, which may be on
 * a different GameMap (this works because GameMap.moveActor() just updates
 * the shared ActorLocationsIterator -- it doesn't care which map the actor
 * came from). Optionally rolls a chance of the actor taking fall damage
 * along the way, without ever preventing the travel itself.
 * <p>
 * Used by Ladder. Not used by TeleportationCircle or AlienDevice, since
 * those have their own side effects (fire, malfunction) that don't fit this
 * shape -- see TeleportAction and AlienDeviceAction.
 */
public class TravelAction extends Action {
    private final Location destination;
    private final String travelMethod;
    private final double fallChance;
    private final int fallDamage;
    private final Random random = new Random();

    /**
     * Creates a travel action with no chance of falling.
     *
     * @param destination where the actor ends up
     * @param travelMethod a short description of how they travelled, e.g. "the ladder"
     */
    public TravelAction(Location destination, String travelMethod) {
        this(destination, travelMethod, 0, 0);
    }

    /**
     * Creates a travel action with a chance of fall damage.
     *
     * @param destination where the actor ends up
     * @param travelMethod a short description of how they travelled
     * @param fallChance probability (0.0-1.0) of falling during the trip
     * @param fallDamage damage taken if the fall happens
     */
    public TravelAction(Location destination, String travelMethod, double fallChance, int fallDamage) {
        this.destination = destination;
        this.travelMethod = travelMethod;
        this.fallChance = fallChance;
        this.fallDamage = fallDamage;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        // The destination is fixed: if someone is standing there, the actor stays where they are.
        if (destination.containsAnActor() || !destination.canActorEnter(actor)) {
            return actor + " cannot use " + travelMethod + ": the other end is occupied.";
        }
        boolean fell = fallChance > 0 && random.nextDouble() < fallChance;
        // The fall never stops the travel -- the actor still arrives either way.
        map.moveActor(actor, destination);
        if (fell) {
            actor.hurt(fallDamage);
            return actor + " loses their footing on " + travelMethod + " and falls, taking " + fallDamage + " damage!";
        }
        return actor + " travels via " + travelMethod + " to " + destination.map() + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses " + travelMethod;
    }
}