package game.state;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Zombie;
import game.actor.ZombieTarget;
import game.blueprint.Consumable;
import game.effect.BurnEffect;
import game.effect.ChargeEffect;
import game.effect.FireEffect;
import game.effect.PoisonEffect;

import java.util.*;
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
     * Checks whether the Zombie meets the fleeing requirements: low health,
     * not burning, and at least one unoccupied enterable adjacent tile without
     * active fire or charge.
     *
     * @param zombie the Zombie being checked
     * @param map    current game map
     * @return true when the Zombie can safely enter its fleeing state
     */
    public static boolean canFlee(Zombie zombie, GameMap map) {
        if (healthPercent(zombie) >= ATTACK_MIN_PERCENT || isBurning(zombie)) {
            return false;
        }
        for (Exit exit : map.locationOf(zombie).getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() || !destination.canActorEnter(zombie)) {
                continue;
            }
            boolean onFire = destination.getGround().statusesOf(FireEffect.class)
                    .stream().anyMatch(FireEffect::isStatusActive);
            boolean charged = destination.getGround().statusesOf(ChargeEffect.class)
                    .stream().anyMatch(ChargeEffect::isStatusActive);
            if (!onFire && !charged) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBurning(Zombie zombie) {
        return zombie.statusesOf(BurnEffect.class).stream().anyMatch(BurnEffect::isStatusActive);
    }

    /**
     * Checks whether the Zombie currently has an active poison effect.
     *
     * @param zombie the Zombie being checked
     * @return true if the Zombie is poisoned
     */
    public static boolean isPoisoned(Zombie zombie) {
        return zombie.statusesOf(PoisonEffect.class).stream().anyMatch(PoisonEffect::isStatusActive);
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
     * @param map    current game map
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
     * @param map    current game map
     * @param range  maximum search distance
     * @return true if a target is found
     */
    public static boolean hasTargetNearby(Zombie zombie, GameMap map, int range) {
        return locationsWithin(zombie, map, range,
                location -> location.containsAnActor() && isTarget(location.getActor()), false);
    }

    /**
     * Checks whether reachable consumable food exists within the given range.
     *
     * @param zombie the Zombie performing the search
     * @param map    current game map
     * @param range  maximum search distance
     * @return true if reachable food is found
     */
    public static boolean hasFoodNearby(Zombie zombie, GameMap map, int range) {

        for (Location location: reachableLocationWithin(zombie,map,range)){
            if(!location.getItemsAs(Consumable.class).isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Searches locations within a given range for a condition.
     *
     * @param zombie               the Zombie performing the search
     * @param map                  current game map
     * @param range                maximum search distance
     * @param condition            condition used to identify a matching location
     * @param matchMustBeEnterable whether the matching tile must be reachable
     * @return true if a matching location is found
     */
    private static boolean locationsWithin(Zombie zombie, GameMap map, int range,
                                           Predicate<Location> condition, boolean matchMustBeEnterable) {

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

                int newDistance = currentDistance + 1;
                distance.put(destination, newDistance);

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

    /**
     * Finds the nearest ZombieTarget within the specified search range
     * using Breadth-First Search (BFS).
     *
     * @param start     the location where the BFS begins
     * @param scanRange the maximum distance from the starting location
     * @return the location of the nearest ZombieTarget, or null if none is found
     */
    public static Location findNearestTarget(Location start, int scanRange) {
        Queue<Location> queue = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.poll();

            if (!current.equals(start)
                    && current.containsAnActor()
                    && isTarget(current.getActor())) {
                return current;
            }
            int distance = Math.max(Math.abs(current.x() - start.x()), Math.abs(current.y() - start.y()));

            if (distance >= scanRange) {
                continue;
            }

            for (Exit exit : current.getExits()) {
                Location next = exit.getDestination();

                if (visited.add(next)) {
                    queue.add(next);
                }
            }

        }
        return null;
    }

    /**
     * Finds all locations reachable by the Zombie within the specified
     * number of BFS steps.
     * <p>
     * The search only traverses locations the Zombie can enter.
     * The starting location is included in the returned list.
     *
     * @param zombie the Zombie performing the search
     * @param map    the current game map
     * @param range  the maximum number of BFS steps
     * @return a list of reachable locations within the specified range
     */
    public static List<Location> reachableLocationWithin(Zombie zombie, GameMap map, int range) {
        List<Location> locations = new ArrayList<>();
        Queue<Location> queue = new ArrayDeque<>();
        Map<Location, Integer> distance = new HashMap<>();

        Location start = map.locationOf(zombie);
        queue.add(start);
        distance.put(start, 0);

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            int steps = distance.get(current);

            locations.add(current);

            if (steps >= range) {
                continue;
            }
            for (Exit exit : current.getExits()) {
                Location next = exit.getDestination();

                if (distance.containsKey(next)
                        || !next.canActorEnter(zombie)) {
                    continue;
                }
                distance.put(next, steps + 1);
                queue.add(next);
            }
        }
        return locations;
    }
}
