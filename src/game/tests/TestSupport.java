package game.tests;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.Farmer;
import game.actor.FinanceStatistics;
import game.terrain.BrewingStation;
import game.terrain.Dirt;
import game.terrain.Rock;
import game.terrain.TileFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Shared helpers for the REQ 4 tests. */
final class TestSupport {
    private TestSupport() { }

    /** A 15 x 9 map of Dirt; every (x,y) in rocks becomes a Rock. */
    static GameMap map(int[]... rocks) throws Exception {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);
        creator.registerGround('R', Rock::new);
        List<String> rows = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            rows.add(".".repeat(15));
        }
        GameMap map = new GameMap("Test", creator, rows);
        for (int[] r : rocks) {
            map.at(r[0], r[1]).setGround(new Rock());
        }
        World world = new World(new Display()) { };
        world.addGameMap(map); // gives the map its actor registry; without this addActor throws NullPointerException
        return map;
    }

    static Farmer farmerAt(GameMap map, int x, int y) throws Exception {
        Farmer farmer = new Farmer("Bob");
        map.at(x, y).addActor(farmer);
        return farmer;
    }

    static void give(Actor actor, int count, Supplier<? extends Item> factory) throws Exception {
        for (int i = 0; i < count; i++) {
            actor.getInventory().add(factory.get());
        }
    }

    static long count(Actor actor, Class<? extends Item> type) throws Exception {
        return actor.getInventory().getItems().stream().filter(i -> i.getClass() == type).count();
    }

    static void addCoins(Actor actor, int coins) throws Exception {
        actor.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.INCREASE, coins);
    }

    static BrewingStation station(Location location) throws Exception {
        return (BrewingStation) location.getGround();
    }

    static long lures(GameMap map) throws Exception {
        return TileFinder.all(map).stream().filter(TileFinder::hasLure).count();
    }

    static void tick(BrewingStation station, Location location, int times) throws Exception {
        for (int i = 0; i < times; i++) {
            station.tick(location);
        }
    }
}