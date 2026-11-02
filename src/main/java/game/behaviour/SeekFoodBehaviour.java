package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Consumable;
import game.farmeraction.EatGroundFoodAction;

import java.util.*;

/**
 * Behaviour that searches for the nearest consumable item on the ground
 * The actor moves toward the nearest reachable consumable item and consumes it
 */
public class SeekFoodBehaviour implements Behaviour<Actor, Action> {
    /**
     * Determine the next action needed to reach and consume the nearest food item
     * @param actor The entity performing the behaviour
     * @param location The location of the current entity
     * @return an action that moves toward or consumes food, or null if no food is found
     */
    @Override
    public Action operate(Actor actor, Location location){
        if(!location.getItemsAs(Consumable.class).isEmpty()){
            Consumable food = location.getItemsAs(Consumable.class).get(0);
            return new EatGroundFoodAction(food,location);
        }

        Queue<Location> queue = new LinkedList<>();
        Set<Location> visited = new HashSet<>();
        Map<Location, Exit> firstMove = new HashMap<>();

        queue.add(location);
        visited.add(location);

        while(!queue.isEmpty()){
            Location current = queue.poll();
            for(Exit exit: current.getExits()){
                Location destination = exit.getDestination();

                if(!visited.contains(destination) && destination.canActorEnter(actor)){
                    visited.add(destination);
                    queue.add(destination);

                    if(current == location){
                        firstMove.put(destination,exit);
                    }else{
                        firstMove.put(destination,firstMove.get(current));
                    }

                    if(!destination.getItemsAs(Consumable.class).isEmpty()){
                        Exit move = firstMove.get(destination);

                        return move.getDestination().getMoveAction(actor, move.getName(), move.getHotKey());
                    }
                }
            }
        }
        return null;
    }
}
