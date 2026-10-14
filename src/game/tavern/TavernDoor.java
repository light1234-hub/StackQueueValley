package game.tavern;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TavernAbility;

/**
 * Represents a doorway that connects the Tavern to another location.
 * Allows adjacent actors with the required capability to travel
 * between linked destinations.
 */
public class TavernDoor extends Ground {
    private final String destinationName;
    private Location destination;

    /**
     * Creates a Tavern doorway with a specified destination name.
     *
     * @param destinationName the name of the destination
     */
    public TavernDoor(String destinationName){
        super('+',"Door to " + destinationName);
        this.destinationName = destinationName;
    }

    /**
     * Links this doorway to a destination location.
     *
     * @param destination the location this doorway leads to
     */
    public void linkTo(Location destination){
        this.destination = destination;
    }

    /**
     * Determines whether an actor can enter the doorway's tile.
     *
     * @param actor the actor attempting to enter
     * @return false because the doorway cannot be walked onto
     */
    @Override
    public boolean canActorEnter(Actor actor){
        return false;
    }

    /**
     * Returns the available actions for an actor interacting
     * with the doorway. Travel is offered only to adjacent actors
     * with Tavern access when a destination has been linked.
     *
     * @param actor the actor interacting with the doorway
     * @param location the location of the doorway
     * @param direction the direction of the doorway from the actor
     * @return the list of available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        if (actor.hasAbility(TavernAbility.CAN_USE_TAVERN) && !direction.isEmpty() && destination != null) {
            actions.add(new TavernTravelAction(location, destination, destinationName));
        }

        return actions;
    }


}
