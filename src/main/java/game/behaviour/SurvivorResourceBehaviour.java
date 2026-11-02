package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import game.actor.PortableResourceTransfer;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.terrain.Soil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Reusable survivor brain: gather resources, return to camp, and repair farm
 * terrain. It depends only on an Actor and its Location rather than a concrete
 * RescuedSurvivor class.
 */
public class SurvivorResourceBehaviour implements Behaviour<Actor, Action> {
    private static final int REPAIR_INTERVAL_TURNS = 4;
    private static final int REPAIR_RADIUS = 2;
    private final int campX;
    private final int campY;
    private int campTurns;

    /** Binds this independent survivor brain to a fixed supply-camp tile. */
    public SurvivorResourceBehaviour(int campX, int campY) {
        this.campX = campX;
        this.campY = campY;
    }

    /** Handles camp maintenance and chooses the next movement, if needed. */
    @Override
    public Action operate(Actor actor, Location current) {
        GameMap map = current.map();
        Location camp = map.at(campX, campY);
        boolean atCamp = current.getGround().hasAbility(GroundAbility.SURVIVOR_CAMP);

        if (atCamp) {
            PortableResourceTransfer.dropAll(actor, current);
            campTurns++;
            if (campTurns % REPAIR_INTERVAL_TURNS == 0) {
                repairNearbyFarm(map, camp);
            }
        } else {
            PortableResourceTransfer.collect(actor, current);
            if (!actor.getInventory().getItems().isEmpty()) {
                Action returnAction = moveTowards(actor, map, current, camp);
                if (returnAction != null) return returnAction;
            }
        }

        if (actor.getInventory().getItems().isEmpty()) {
            Location resource = findNearestResource(actor, map, current);
            if (resource != null) {
                Action collectAction = moveTowards(actor, map, current, resource);
                if (collectAction != null) return collectAction;
            }
        } else if (!atCamp) {
            Action returnAction = moveTowards(actor, map, current, camp);
            if (returnAction != null) return returnAction;
        }

        if (!atCamp) return moveTowards(actor, map, current, camp);
        return null;
    }

    /**
     * Finds the nearest unoccupied tile containing a portable item using breadth-first search.
     *
     * @param map map searched for resources
     * @param start survivor's current location
     * @return nearest resource tile, or {@code null} if none is reachable
     */
    private Location findNearestResource(Actor actor, GameMap map, Location start) {
        Queue<Location> queue = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.remove();
            if (current != start
                    && !current.getGround().hasAbility(GroundAbility.SURVIVOR_CAMP)
                    && !current.containsAnActor()
                    && current.getItems().stream().anyMatch(
                    item -> item.hasAbility(ItemAbility.PORTABLE))) {
                return current;
            }
            for (Exit exit : current.getExits()) {
                Location next = exit.getDestination();
                if (!visited.contains(next)
                        && !next.containsAnActor()
                        && next.canActorEnter(actor)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return null;
    }

    /**
     * Finds a shortest next move toward the target tile using breadth-first search.
     *
     * @param map map used for pathfinding
     * @param start survivor's current location
     * @param target destination location
     * @return next movement action, or {@code null} if the target is unreachable
     */
    private Action moveTowards(Actor actor, GameMap map, Location start, Location target) {
        if (start == target) {
            return null;
        }

        Queue<Location> queue = new ArrayDeque<>();
        Map<Location, Location> previous = new HashMap<>();
        queue.add(start);
        previous.put(start, null);

        while (!queue.isEmpty()) {
            Location current = queue.remove();
            if (current == target) {
                break;
            }
            for (Exit exit : current.getExits()) {
                Location next = exit.getDestination();
                if (!previous.containsKey(next)
                        && !next.containsAnActor()
                        && next.canActorEnter(actor)) {
                    previous.put(next, current);
                    queue.add(next);
                }
            }
        }

        if (!previous.containsKey(target)) {
            return null;
        }

        Location step = target;
        while (previous.get(step) != start) {
            step = previous.get(step);
            if (step == null) {
                return null;
            }
        }
        return new MoveActorAction(step, "toward " + target.getGround());
    }

    /**
     * Repairs one damaged farm tile within two tiles of the supply camp.
     *
     * @param map map containing the camp and damaged terrain
     * @param camp location of the supply camp
     */
    private void repairNearbyFarm(GameMap map, Location camp) {
        for (int radius = 1; radius <= REPAIR_RADIUS; radius++) {
            for (int y = Math.max(map.getYRange().min(), camp.y() - radius);
                 y <= Math.min(map.getYRange().max(), camp.y() + radius); y++) {
                for (int x = Math.max(map.getXRange().min(), camp.x() - radius);
                     x <= Math.min(map.getXRange().max(), camp.x() + radius); x++) {
                    if (Math.abs(x - camp.x()) + Math.abs(y - camp.y()) > radius) {
                        continue;
                    }
                    Location target = map.at(x, y);
                    Ground ground = target.getGround();
                    if (!target.containsAnActor()
                            && ground.hasAbility(GroundAbility.SURVIVOR_REPAIRABLE)) {
                        target.setGround(new Soil());
                        return;
                    }
                }
            }
        }
    }
}
