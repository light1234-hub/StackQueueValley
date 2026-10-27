
package game.arena;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Farmer;
import game.terrain.QuestArenaWall;
import game.terrain.Dirt;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Manages the reusable quest arena, including terrain placement,
 * actor spawning, item placement and cleanup between quests.
 */
public class ArenaQuest {
    private final GameMap map;
    private final Farmer farmer;

    /**
     * Creates a quest arena using an existing map and farmer.
     *
     * @param map the quest arena map
     * @param farmer the player
     */
    public ArenaQuest(GameMap map, Farmer farmer) {
        this.map = Objects.requireNonNull(map);
        this.farmer = Objects.requireNonNull(farmer);
    }

    /**
     * Returns the arena map.
     *
     * @return the game map
     */
    public GameMap map() {
        return map;
    }

    /**
     * Checks whether coordinates are inside the arena.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return true if the coordinates are valid
     */
    public boolean inside(int x, int y) {
        return map.getXRange().contains(x)
                && map.getYRange().contains(y);
    }

    /**
     * Places ground at the specified location.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param ground ground to place
     * @return true if placement succeeds
     */
    public boolean placeGround(int x, int y, Ground ground) {
        if (!inside(x, y) || ground == null) {
            return false;
        }

        Location location = map.at(x, y);

        if (location.containsAnActor()) {
            return false;
        }

        location.setGround(ground);
        return true;
    }

    /**
     * Spawns an actor at the specified location.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param actor actor to spawn
     * @return true if spawning succeeds
     */
    public boolean spawnActor(int x, int y, Actor actor) {
        if (!inside(x, y) || actor == null) {
            return false;
        }

        Location location = map.at(x, y);

        if (location.containsAnActor() || !location.canActorEnter(actor)) {
            return false;
        }

        try {
            map.addActor(actor, location);
            return true;
        } catch (GameEngineException exception) {
            return false;
        }
    }

    /**
     * Places an item at the specified location.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param item item to place
     * @return true if placement succeeds
     */
    public boolean placeItem(int x, int y, Item item) {
        if (!inside(x, y) || item == null) {
            return false;
        }

        Location location = map.at(x, y);
        location.addItem(item);
        return true;
    }

    /**
     * Removes temporary actors and items, then restores the arena
     * terrain and its walls between contracts.
     *
     * @throws IllegalStateException if the farmer is still in the arena
     */
    public void reset() {
        if (map.contains(farmer)) {
            throw new IllegalStateException(
                    "Cannot reset arena while the Farmer is inside"
            );
        }

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location location = map.at(x, y);

                if (location.containsAnActor()) {
                    map.removeActor(location.getActor());
                }

                for (Item item : new ArrayList<>(location.getItems())) {
                    location.removeItem(item);
                }

                boolean wall = x == map.getXRange().min()
                        || y == map.getYRange().min()
                        || x == map.getXRange().max()
                        || y == map.getYRange().max();

                location.setGround(
                        wall ? new QuestArenaWall() : new Dirt()
                );
            }
        }
    }
}