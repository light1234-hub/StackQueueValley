package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.Random;

/**
 * Behaviour that moves an actor randomly to an accessible neighbouring location.
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {
    private Random random;

    /**
     * Creates a wandering behaviour with a random movement selector.
     */
    public WanderBehaviour(){
        random = new Random();
    }

    /**
     * Selects a random valid movement from the actor's current location.
     *
     * @param actor actor that will move
     * @param location actor's current location
     * @return a movement action, or {@code null} if no valid movement exists
     */
    @Override
    public Action operate(Actor actor, Location location){
        ArrayList<Action> actions = new ArrayList<>();

        for(Exit exit: location.getExits()){
            Location destination = exit.getDestination();
            if(destination.canActorEnter(actor)){
                actions.add(exit.getDestination().getMoveAction(actor,exit.getName(),exit.getHotKey()));
            }
        }
        if(!actions.isEmpty()){
            return actions.get((random.nextInt(actions.size())));
        }
        else{
            return null;
        }
    }
}
