package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.Random;


/**
 * Represents the Wandering behaviour of Animals throughout the Map
 */
public class WanderBehaviour implements Behaviour<Actor, Action>
{
    private final Random random = new Random();

    /**
     *A method that determines the next move of the actor
     * by going through a list of possible exits and
     * randomly selecting one
     * @param actor The entity performing the behaviour
     * @param location The location of the current entity
     * @return a Valid exit or Null if there are no valid exits
     */
    @Override
    public Action operate(Actor actor, Location location)
    {
        ArrayList<Exit> validExits = new ArrayList<>();

        for (Exit exit : location.getExits())
        {
            Location destination = exit.getDestination();

            if (destination.canActorEnter(actor))
            {
                validExits.add(exit);
            }
        }

        if (!validExits.isEmpty())
        {
            Exit selectedExit = validExits.get(random.nextInt(validExits.size()));

            return new MoveActorAction(selectedExit.getDestination(),selectedExit.getName());
        }
        else
        {
            return null;
        }

    }
}
