package game.quest;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;

import java.util.function.Supplier;

/**
 * Provides quest consequences with a restricted API for changing the persistent farm.
 * Terrain-selection rules are capability-based, so callers do not need concrete-type checks.
 */
public class QuestWorldContext {
    private final GameMap farm;

    /**
     * Creates a context for the inherited farm.
     *
     * @param farm persistent farm map to modify
     * @throws NullPointerException if the map is {@code null}
     */
    public QuestWorldContext(GameMap farm) {
        this.farm = farm;
    }

    /**
     * Changes an unoccupied farm tile to the requested ground.
     *
     * @param x horizontal tile coordinate
     * @param y vertical tile coordinate
     * @param ground replacement ground
     * @return {@code true} if the tile was changed
     */
    public boolean changeGround(int x, int y, Ground ground) {
        if (ground == null || !inside(x, y)) {
            return false;
        }
        Location target = farm.at(x, y);
        if (target.containsAnActor()) {
            return false;
        }
        target.setGround(ground);
        return true;
    }

    /**
     * Places a persistent reward item on a valid farm tile.
     *
     * @param x horizontal tile coordinate
     * @param y vertical tile coordinate
     * @param item item to place
     * @return {@code true} if the item was placed
     */
    public boolean placeItem(int x, int y, Item item) {
        if (item == null || !inside(x, y)) {
            return false;
        }
        farm.at(x, y).addItem(item);
        return true;
    }

    /**
     * Adds an actor to a valid tile if the tile is unoccupied and enterable.
     *
     * @param x horizontal tile coordinate
     * @param y vertical tile coordinate
     * @param actor actor to spawn
     * @return {@code true} if the actor was added
     */
    public boolean spawnActor(int x, int y, Actor actor) {
        if (actor == null || !inside(x, y)) {
            return false;
        }
        Location tile = farm.at(x, y);
        if (tile.containsAnActor() || !tile.canActorEnter(actor)) {
            return false;
        }
        try {
            farm.addActor(actor, tile);
            return true;
        } catch (GameEngineException error) {
            return false;
        }
    }

    /**
     * Attempts to spawn an actor at the requested coordinate and then at nearby tiles.
     *
     * @param x preferred horizontal coordinate
     * @param y preferred vertical coordinate
     * @param actor actor to spawn
     * @return {@code true} if the actor was spawned within the search radius
     */
    public boolean spawnNearby(int x, int y, Actor actor) {
        if (actor == null) {
            return false;
        }
        for (int radius = 0; radius <= 2; radius++) {
            for (int yy = y - radius; yy <= y + radius; yy++) {
                for (int xx = x - radius; xx <= x + radius; xx++) {
                    if (Math.abs(xx - x) + Math.abs(yy - y) <= radius
                            && spawnActor(xx, yy, actor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Places a ground feature on the requested tile or a nearby eligible tile.
     * Only grounds advertising {@link GroundAbility#QUEST_FEATURE_PLACEABLE} can be replaced.
     *
     * @param x preferred horizontal coordinate
     * @param y preferred vertical coordinate
     * @param groundFactory factory that creates the ground feature
     * @return location used, or {@code null} if no eligible tile is available
     */
    public Location placeGroundNearby(int x, int y, Supplier<? extends Ground> groundFactory) {
        if (groundFactory == null) {
            return null;
        }
        for (int radius = 0; radius <= 2; radius++) {
            for (int yy = y - radius; yy <= y + radius; yy++) {
                for (int xx = x - radius; xx <= x + radius; xx++) {
                    if (Math.abs(xx - x) + Math.abs(yy - y) > radius || !inside(xx, yy)) {
                        continue;
                    }
                    Location target = farm.at(xx, yy);
                    if (!target.containsAnActor()
                            && target.getGround().hasAbility(
                            GroundAbility.QUEST_FEATURE_PLACEABLE)) {
                        target.setGround(groundFactory.get());
                        return target;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Places a ground feature on an eligible tile adjacent to an existing anchor.
     *
     * @param anchor location the new tile must touch
     * @param groundFactory factory that creates the new ground feature
     * @return the adjacent location used, or {@code null} if none is available
     */
    public Location placeAdjacentGround(
            Location anchor, Supplier<? extends Ground> groundFactory) {
        if (anchor == null || groundFactory == null) {
            return null;
        }
        for (Exit exit : anchor.getExits()) {
            Location target = exit.getDestination();
            if (!target.containsAnActor()
                    && target.getGround().hasAbility(
                    GroundAbility.QUEST_FEATURE_PLACEABLE)) {
                target.setGround(groundFactory.get());
                return target;
            }
        }
        return null;
    }

    /**
     * Checks whether coordinates belong to the persistent farm map.
     *
     * @param x horizontal tile coordinate
     * @param y vertical tile coordinate
     * @return {@code true} if both coordinates are in range
     */
    public boolean inside(int x, int y) {
        return farm.getXRange().contains(x) && farm.getYRange().contains(y);
    }
}