package game.terrain;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.AlchemyAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper for cauldrons: finds map tiles. "Empty" = plain Dirt, no actor, no items.
 * Concrete terrain types are compared by exact class (no instanceof).
 */
public final class TileFinder {
    private static final Random RNG = new Random();

    private TileFinder() { }

    public static List<Location> all(GameMap map) {
        List<Location> tiles = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                tiles.add(map.at(x, y));
            }
        }
        return tiles;
    }

    public static boolean isDirt(Location l) {
        return l.getGround().getClass() == Dirt.class;
    }

    public static boolean isRock(Location l) {
        return l.getGround().getClass() == Rock.class;
    }

    public static boolean hasLure(Location l) {
        return l.getItems().stream().anyMatch(i -> i.hasAbility(AlchemyAbility.LURE));
    }

    public static List<Location> rocks(GameMap map) {
        List<Location> rocks = new ArrayList<>();
        for (Location l : all(map)) {
            if (isRock(l)) {
                rocks.add(l);
            }
        }
        return rocks;
    }

    /** @return a random empty tile, or null if none. */
    public static Location randomEmptyTile(GameMap map) {
        List<Location> empty = new ArrayList<>();
        for (Location l : all(map)) {
            if (isDirt(l) && !l.containsAnActor() && l.getItems().isEmpty()) {
                empty.add(l);
            }
        }
        return empty.isEmpty() ? null : empty.get(RNG.nextInt(empty.size()));
    }
}