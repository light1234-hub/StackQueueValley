package game.farmeraction;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;


public final class SafeArrival {
    private static final int MAX_SEARCH_DISTANCE = 10;

    private SafeArrival() {
    }

    /**
     * @param location the location to check
     * @param actor the actor who wants to stand there
     * @return true if the actor may enter the location and nobody is standing on it
     */
    public static boolean isFree(Location location, Actor actor) {
        return location.canActorEnter(actor) && !location.containsAnActor();
    }

    /**
     * Returns the target itself if it is free, otherwise the nearest free
     * location around it (searching outward, on the target's own map).
     *
     * @param target the intended arrival location
     * @param actor the actor who is arriving
     * @return a free location, or null if there is none within the search distance
     */
    public static Location resolve(Location target, Actor actor) {
        if (isFree(target, actor)) {
            return target;
        }

        Map<Location, Integer> distance = new HashMap<>();
        Queue<Location> queue = new ArrayDeque<>();
        distance.put(target, 0);
        queue.add(target);

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            int d = distance.get(current);
            if (d >= MAX_SEARCH_DISTANCE) {
                continue;
            }
            for (Exit exit : current.getExits()) {
                Location next = exit.getDestination();
                if (distance.containsKey(next)) {
                    continue;
                }
                if (isFree(next, actor)) {
                    return next;
                }
                distance.put(next, d + 1);
                queue.add(next);
            }
        }
        return null;
    }
}


