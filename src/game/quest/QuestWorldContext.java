package game.quest;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Gives quest consequences a narrow API for changing the persistent farm.
 * Invalid or occupied terrain changes are ignored safely.
 */
public class QuestWorldContext {
    private final GameMap farm;

    /** @param farm the persistent inherited farm map */
    public QuestWorldContext(GameMap farm) {
        this.farm = farm;
    }

    /** Changes an unoccupied farm tile without moving any actors. */
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

    /** Places a persistent reward item on a valid farm tile. */
    public boolean placeItem(int x, int y, Item item) {
        if (item == null || !inside(x, y)) {
            return false;
        }
        farm.at(x, y).addItem(item);
        return true;
    }

    /** Places a creature only where it can safely stand. */
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

    /** Finds a free tile near a requested spawn point, if the initial point is blocked. */
    public boolean spawnNearby(int x, int y, Actor actor) {
        if (actor == null) {
            return false;
        }
        for (int radius = 0; radius <= 2; radius++) {
            for (int yy = y - radius; yy <= y + radius; yy++) {
                for (int xx = x - radius; xx <= x + radius; xx++) {
                    if (spawnActor(xx, yy, actor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Checks whether coordinates belong to the farm. */
    public boolean inside(int x, int y) {
        return farm.getXRange().contains(x) && farm.getYRange().contains(y);
    }
}