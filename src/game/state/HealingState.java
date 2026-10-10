package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.behaviour.SeekFoodBehaviour;
import game.blueprint.Consumable;
import game.item.ParsnipItem;

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
     * Performs the one-time effect when the Zombie enters HealingState.
     * Uses BFS to search reachable locations within two steps and moves
     * any consumable items found onto the Zombie's current tile.
     * If no food is collected, two ParsnipItems are generated on its tile.
     *
     * @param zombie the Zombie entering the healing state
     * @param map the current game map
     */
    @Override
    public void onEnter(Zombie zombie,GameMap map) {
        Location zombieLoc = map.locationOf(zombie);
        int foodMove = 0;

        List<Location> reachableLocations = ZombieSensor.reachableLocationWithin(zombie, map, FOOD_SCAN_RANGE);

        for (Location location : reachableLocations) {
            if (location.equals(zombieLoc)) {
                continue;
            }

            List<Consumable> foods = new ArrayList<>(location.getItemsAs(Consumable.class));

            for (Consumable food : foods) {
                location.removeItem(food.getItem());
                zombieLoc.addItem(food.getItem());
                foodMove++;
            }
        }
        if (foodMove == 0) {
            zombieLoc.addItem(new ParsnipItem());
            zombieLoc.addItem(new ParsnipItem());
        }
    }
}
