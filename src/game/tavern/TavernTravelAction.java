package game.tavern;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TavernAbility;

/**
 * Allows authorised actors to travel between linked Tavern locations.
 * Validates the actor's permission, position, and destination before
 * moving the actor without applying additional travel effects.
 */
public class TavernTravelAction extends Action {

    private final Location doorway;
    private final Location destination;
    private final String name;
    private final Ground doorGround;

    /**
     * Creates an action for travelling through a Tavern doorway.
     *
     * @param doorway     the location of the doorway
     * @param destination the location to travel to
     * @param name        the name of the destination
     */

    public TavernTravelAction(Location doorway, Location destination, String name) {

        this.doorway = doorway;
        this.destination = destination;
        this.name = name;
        this.doorGround = doorway.getGround();
    }

    /**
     * Attempts to move an authorised actor to the linked destination.
     * Travel fails if the actor is not beside the original doorway
     * or if the destination cannot be entered.
     *
     * @param actor the actor attempting to travel
     * @param map   the current game map
     * @return a message describing the result of the travel attempt
     */

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(TavernAbility.CAN_USE_TAVERN) || !map.contains(actor)) {
            return "This actor cannot use the Tavern Doorway!!";
        }
        Location here = map.locationOf(actor);

        if (doorway.map() != map || doorway.getGround() != doorGround || Math.max(Math.abs(here.x() - doorway.x()), Math.abs(here.y() - doorway.y())) != 1) {
            return "The actor must stand beside the doorway to travel";
        }

        if (destination.containsAnActor() || !destination.canActorEnter(actor)) {
            return "Cannot travel to " + name + ": the destination is blocked.";
        }

        map.moveActor(actor, destination);

        return actor + " travel to " + name + ".";
    }
    /**
     * Returns the description of the travel action displayed in the menu.
     *
     * @param actor the actor performing the action
     * @return the description of the destination
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Travel to " + name;
    }


}
