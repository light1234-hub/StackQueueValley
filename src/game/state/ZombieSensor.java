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
    /** How many tiles away the zombie can notice targets and food. */
    public static final int SCAN_RANGE = 3;
    /** Below this health % the zombie prioritises healing over fleeing. */
    public static final int HEAL_BELOW_PERCENT = 50;
    /** With a target nearby, the zombie attacks at or above this health %, and flees below it. */
    public static final int ATTACK_MIN_PERCENT = 70;
    /** The zombie counts as recovered at or above this health %. */
    public static final int RECOVERED_PERCENT = 70;

    private ZombieSensor() {
    }


    public static int healthPercent(Zombie zombie) {
        int max = zombie.getMaximumStatistic(ActorStatistics.HEALTH);
        if (max <= 0) {
            return 0;
        }
        return zombie.getStatistic(ActorStatistics.HEALTH) * 100 / max;
    }

    /**
     * @param actor the actor to check
     * @return true if the zombie should treat this actor as prey
     */
    public static boolean isTarget(Actor actor) {
        return actor.hasAbility(ZombieTarget.ZOMBIE_TARGET);
    }


    public static Exit adjacentTargetExit(Zombie zombie, GameMap map) {
        for (Exit exit : map.locationOf(zombie).getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() && isTarget(destination.getActor())) {
                return exit;
            }
        }
        return null;
    }


    public static boolean hasTargetNearby(Zombie zombie, GameMap map) {
        return !locationsWithin(zombie, map, SCAN_RANGE,
                location -> location.containsAnActor() && isTarget(location.getActor())).isEmpty();
    }


    public static boolean hasFoodNearby(Zombie zombie, GameMap map) {
        return !locationsWithin(zombie, map, SCAN_RANGE,
                location -> !location.getItemsAs(Consumable.class).isEmpty()).isEmpty();
    }


    private static List<Location> locationsWithin(Zombie zombie, GameMap map, int range,
                                                  Predicate<Location> condition) {
        List<Location> matches = new ArrayList<>();
        Location start = map.locationOf(zombie);
        Map<Location, Integer> distance = new HashMap<>();
        Queue<Location> queue = new ArrayDeque<>();

        distance.put(start, 0);
        queue.add(start);
        if (condition.test(start)) {
            matches.add(start);
        }

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            int d = distance.get(current);
            if (d == range) {
                continue;
            }
            for (Exit exit : current.getExits()) {
                Location destination = exit.getDestination();
                if (distance.containsKey(destination)) {
                    continue;
                }
                distance.put(destination, d + 1);
                if (condition.test(destination)) {
                    matches.add(destination);
                }
                if (destination.canActorEnter(zombie)) {
                    queue.add(destination);
                }
            }
        }
        return matches;
    }
}