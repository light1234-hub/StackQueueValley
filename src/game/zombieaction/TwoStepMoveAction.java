package game.zombieaction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

public class TwoStepMoveAction extends Action
{
    private final Location destination;
    private final String directionName;

    public TwoStepMoveAction(Location destination, String directionName)
    {
        this.destination = destination;
        this.directionName = directionName;
    }

    @Override
    public String execute(Actor actor, GameMap map)
    {
        map.moveActor(actor, destination);
        return actor + " panics and flees across 2 tiles " + directionName + ".";
    }

    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " flees 2 tiles " + directionName;
    }
}
