package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.behaviour.SeekFoodBehaviour;
import game.blueprint.Consumable;

import java.util.*;


/**
 * Represents the healing state of a zombie
 * While in this state, the zombie attempts to recover by searching
 * for consumable items and may transition to another state depending
 * on the current game condition
 */
public class HealingState implements CreatureState{
    private final SeekFoodBehaviour seekFoodBehaviour;

    private static final int FOOD_SCAN_RANGE = 2;
    private static final int TARGET_SCAN_RANGE = 3;

    /**
     * creates a new healing state
     */
    public HealingState(){
        this.seekFoodBehaviour = new SeekFoodBehaviour();
    }

    /**
     * Determine whether the zombie remains in the healing state
     * or transitions to another state
     *
     * @param zombie the Zombie currently in this state
     * @param map the current game map
     * @return the next state of the zombie
     */
    @Override
    public  CreatureState nextState(Zombie zombie, GameMap map){
        boolean recovered = ZombieSensor.healthPercent(zombie) >= ZombieSensor.RECOVERED_PERCENT;
        boolean target = ZombieSensor.hasTargetNearby(zombie,map,TARGET_SCAN_RANGE);
        boolean food = ZombieSensor.hasFoodNearby(zombie,map,FOOD_SCAN_RANGE);

        if(recovered && target){
            return zombie.getAttackingState();
        }

        if (recovered && !target && !food){
            return zombie.getWanderingState();
        }


        return this;
    }

    /**
     * Determine the action performed by the zombie while healing
     *
     * @param zombie the Zombie performing the action
     * @param map the current game map
     * @return the action performed by the zombie
     */
    @Override
    public Action getAction(Zombie zombie,GameMap map){
        Action action = seekFoodBehaviour.operate(zombie,map.locationOf(zombie));

        if (action != null){
            return action;
        }

        return new DoNothingAction();
    }

    /**
     * Performs the one-time effect when the Zombie enters the healing state
     * When the zombie enters HealingState, it checks all adjacent tiles and pulls
     * any consumable items onto its own tile
     * @param zombie zombie the Zombie entering the state
     * @param map map the current game map
     */
    @Override
    public void onEnter(Zombie zombie,GameMap map){
        Location zombieLoc = map.locationOf(zombie);

        for(Exit exit:zombieLoc.getExits()){
            Location adjacentLoc =  exit.getDestination();
            List<Consumable> foods = new ArrayList<>(adjacentLoc.getItemsAs(Consumable.class));
            for(Consumable food: foods){
                adjacentLoc.removeItem(food.getItem());
                zombieLoc.addItem(food.getItem());
            }
        }

    }

}
