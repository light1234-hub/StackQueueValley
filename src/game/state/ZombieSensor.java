package game.state;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.actor.ZombieTarget;
import game.blueprint.Consumable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;


public final class ZombieSensor {
    public static final int HEAL_BELOW_PERCENT = 50;
    public static final int ATTACK_MIN_PERCENT = 70;
    public static final int RECOVERED_PERCENT = 70;

    private ZombieSensor() {
    }

    /**
     * Returns the Zombie's current health as a percentage.
     *
     * @param zombie the Zombie being checked
     * @return current health percentage
     */
    public static int healthPercent(Zombie zombie) {
        int max = zombie.getMaximumStatistic(ActorStatistics.HEALTH);
        if (max <= 0) {
            return 0;
        }
        return zombie.getStatistic(ActorStatistics.HEALTH) * 100 / max;
    }

    /**
     * Checks whether an actor is a valid Zombie target.
     *
     * @param actor actor being checked
     * @return true if the actor is a Zombie target
     */
    public static boolean isTarget(Actor actor) {
        return actor.hasAbility(ZombieTarget.ZOMBIE_TARGET);
    }

    /**
     * Finds an adjacent exit leading to a Zombie target.
     *
     * @param zombie the Zombie performing the search
     * @param map current game map
     * @return exit leading to a target, or null if none exists
     */
    public static Exit adjacentTargetExit(Zombie zombie, GameMap map) {
        for (Exit exit : map.locationOf(zombie).getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() && isTarget(destination.getActor())) {
                return exit;
            }
        }
        return null;
    }

    /**
     * Checks whether a Zombie target exists within the given range.
     *
     * @param zombie the Zombie performing the search
     * @param map current game map
     * @param range maximum search distance
     * @return true if a target is found
     */
    public static boolean hasTargetNearby(Zombie zombie, GameMap map,int range) {
        return locationsWithin(zombie, map, range,
                location -> location.containsAnActor() && isTarget(location.getActor()),false);
    }

    /**
     * Checks whether reachable consumable food exists within the given range.
     *
     * @param zombie the Zombie performing the search
     * @param map current game map
     * @param range maximum search distance
     * @return true if reachable food is found
     */
    public static boolean hasFoodNearby(Zombie zombie, GameMap map,int range) {
        return !locationsWithin(zombie, map, range,
                location -> !location.getItemsAs(Consumable.class).isEmpty(),true);
    }

    /**
     * Searches locations within a given range for a condition.
     *
     * @param zombie the Zombie performing the search
     * @param map current game map
     * @param range maximum search distance
     * @param condition condition used to identify a matching location
     * @param matchMustBeEnterable whether the matching tile must be reachable
     * @return true if a matching location is found
     */
    private static boolean locationsWithin(Zombie zombie, GameMap map, int range,
                                                  Predicate<Location> condition,boolean matchMustBeEnterable) {

        Location start = map.locationOf(zombie);

        Queue<Location> queue = new ArrayDeque<>();
        Map<Location, Integer> distance = new HashMap<>();



        queue.add(start);
        distance.put(start, 0);

        if (condition.test(start)) {
            return true;
        }

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            int currentDistance = distance.get(current);
            if (currentDistance >= range) {
                continue;
            }
            for (Exit exit : current.getExits()) {
                Location destination = exit.getDestination();
                if (distance.containsKey(destination)) {
                    continue;
                }

                int newDistance =  currentDistance + 1;
                distance.put(destination,newDistance);

                boolean enterable = destination.canActorEnter(zombie);

                if (condition.test(destination) && (!matchMustBeEnterable || enterable)) {
                    return true;
                }
                if (enterable) {
                    queue.add(destination);
                }
            }
        }
        return false;
    }
}