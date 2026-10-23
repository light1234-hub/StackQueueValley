package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
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
 * An autonomous survivor who retrieves loose portable resources, brings them to
 * a supply camp, and repairs damaged nearby farm terrain over time.
 */
public class RescuedSurvivor extends Actor {
    private static final int REPAIR_INTERVAL_TURNS = 4;
    private static final int REPAIR_RADIUS = 2;

    private final int campX;
    private final int campY;
    private int campTurns;

    /**
     * Creates a survivor with the coordinates of its supply camp.
     *
     * @param campX horizontal coordinate of the supply camp
     * @param campY vertical coordinate of the supply camp
     */
    public RescuedSurvivor(int campX, int campY) {
        super("Rescued Survivor", 'v', 40);
        this.campX = campX;
        this.campY = campY;
    }

    /**
     * Collects portable items, returns to camp when carrying resources, and
     * repairs eligible terrain periodically while at camp.
     *
     * @param actions actions available this turn
     * @param lastAction action performed on the previous turn
     * @param map map containing this survivor
     * @param display display used by the game engine
     * @return the action selected for this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            unconscious(map);
            return new DoNothingAction();
        }

        Location current = map.locationOf(this);
        Location camp = map.at(campX, campY);
        boolean atCamp = current.getGround().hasAbility(GroundAbility.SURVIVOR_CAMP);

        if (atCamp) {
            depositResources(current);
            campTurns++;
            if (campTurns % REPAIR_INTERVAL_TURNS == 0) {
                repairNearbyFarm(map, camp);
            }
        } else {
            collectResources(current);
            if (!getInventory().getItems().isEmpty()) {
                Action returnAction = moveTowards(map, current, camp);
                if (returnAction != null) {
                    return returnAction;
                }
            }
        }

        if (getInventory().getItems().isEmpty()) {
            Location resource = findNearestResource(map, current);
            if (resource != null) {
                Action collectAction = moveTowards(map, current, resource);
                if (collectAction != null) {
                    return collectAction;
                }
            }
        } else if (!atCamp) {
            Action returnAction = moveTowards(map, current, camp);
            if (returnAction != null) {
                return returnAction;
            }
        }

        if (!atCamp) {
            Action campAction = moveTowards(map, current, camp);
            if (campAction != null) {
                return campAction;
            }
        }
        return new DoNothingAction();
    }

    /**
     * Collects all portable items from the survivor's current tile.
     *
     * @param location tile from which items are collected
     */
    private void collectResources(Location location) {
        for (Item item : new ArrayList<>(location.getItems())) {
            if (item.hasAbility(ItemAbility.PORTABLE)) {
                location.removeItem(item);
                getInventory().add(item);
            }
        }
    }

    /**
     * Deposits all carried items onto the supply camp tile.
     *
     * @param camp camp tile receiving the resources
     */
    private void depositResources(Location camp) {
        for (Item item : new ArrayList<>(getInventory().getItems())) {
            getInventory().remove(item);
            camp.addItem(item);
        }
    }

    /**
     * Finds the nearest unoccupied tile containing a portable item using breadth-first search.
     *
     * @param map map searched for resources
     * @param start survivor's current location
     * @return nearest resource tile, or {@code null} if none is reachable
     */
    private Location findNearestResource(GameMap map, Location start) {
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
                        && next.canActorEnter(this)) {
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
    private Action moveTowards(GameMap map, Location start, Location target) {
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
                        && next.canActorEnter(this)) {
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

