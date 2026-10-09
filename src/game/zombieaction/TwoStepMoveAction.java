package game.zombieaction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An action that allows the actor to traverse 2 steps within one turn
 */
public class TwoStepMoveAction extends Action
{
    private final Location destination;
    private final String directionName;

    /**
     * A constructor that creates an object of the two step move action
     * @param destination
     * @param directionName
     */
    public TwoStepMoveAction(Location destination, String directionName)
    {
        this.destination = destination;
        this.directionName = directionName;
    }

    /**
     * Executes the two step action and moves the actor to the new location on the map
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a string describing the result of the two step action
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        map.moveActor(actor, destination);
        return actor + " panics and flees across 2 tiles " + directionName + ".";
    }

    /**
     *Provides a description of the action in the menu class
     * @param actor The actor performing the action.
     * @return a string description of the 2 step movement in the menu
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " flees 2 tiles " + directionName;
    }
}
