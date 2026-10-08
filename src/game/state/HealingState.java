package game.state;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.actor.ZombieTarget;
import game.behaviour.SeekFoodBehaviour;
import game.blueprint.Consumable;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents the healing state of a zombie
 * While in this state, the zombie attempts to recover by searching
 * for consumable items and may transition to another state depending
 * on the current game condition
 */
public class HealingState implements CreatureState{
    private final SeekFoodBehaviour seekFoodBehaviour;
    private static final int RECOVERY_PERCENT = 70;
    private static final int SCAN_RANGE = 3;

    /**
     * creates a new healing state
     */
    public HealingState(){
        this.seekFoodBehaviour = new SeekFoodBehaviour();
    }

    /**
     * Checks whether the Zombie has recovered to the required health percentage.
     *
     * @param zombie the Zombie being checked
     * @return true if the Zombie has reached the recovery threshold
     */
    private boolean isRecovered(Zombie zombie){
        int currentHp = zombie.getStatistic(ActorStatistics.HEALTH);
        int maxHP = zombie.getMaximumStatistic(ActorStatistics.HEALTH);

        return currentHp * 100 >= maxHP * RECOVERY_PERCENT;
    }

    /**
     * Searches locations within the scan range for a location
     * satisfying the given condition.
     *
     * @param zombie the Zombie performing the search
     * @param map the current game map
     * @param condition condition used to identify a matching location
     * @return true if a matching location is found
     */
    private boolean hasNearbyMatch(Zombie zombie, GameMap map, Predicate<Location> condition){
        Location start = map.locationOf(zombie);

        Queue<Location> queue = new ArrayDeque<>();

        Queue<Integer> distances = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();

        queue.add(start);
        distances.add(0);
        visited.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            int distance = distances.poll();

            if (condition.test(current)) {
                return true;
            }

            if (distance == SCAN_RANGE) {
                continue;
            }

            for (Exit exit : current.getExits()) {
                Location destination = exit.getDestination();

                if(condition.test(destination)){
                    return true;
                }

                if (!visited.contains(destination) && destination.canActorEnter(zombie)) {
                    visited.add(destination);
                    queue.add(destination);
                    distances.add(distance + 1);
                }
            }
        }

        return false;
    }
    /**
     * Checks for consumable food within the Zombie's scan range.
     *
     * @param zombie the Zombie performing the search
     * @param map the current game map
     * @return true if consumable food is nearby
     */
    private boolean hasFoodNearby(Zombie zombie,GameMap map){
        return hasNearbyMatch(zombie,map,location -> !location.getItemsAs(Consumable.class).isEmpty());
    }

    /**
     * Checks for a valid Zombie target within the scan range.
     *
     * @param zombie the Zombie performing the search
     * @param map the current game map
     * @return true if a valid target is nearby
     */
    private boolean hasTargetNearby(Zombie zombie, GameMap map){
        return hasNearbyMatch(zombie,map,location -> location.containsAnActor() && location.getActor().hasAbility(ZombieTarget.ZOMBIE_TARGET));
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
        boolean recovered = isRecovered(zombie);
        boolean target = hasTargetNearby(zombie,map);
        boolean food = hasFoodNearby(zombie,map);


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
