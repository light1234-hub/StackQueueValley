package game.terrain;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Base class for plants that age and perform autonomous actions during map ticks.
 * It owns the shared age counter, action helpers, and location-aware reporting.
 */
public abstract class Plant extends Ground {
    /** Number of world turns this plant instance has lived. */
    private int age;

    /** Random source shared by this plant's growth, spread, and production attempts. */
    private final Random random = new Random();

    /**
     * Creates a plant with its map character and display name.
     *
     * @param displayChar character shown for this plant on the map
     * @param name display name used by the map and action messages
     */
    protected Plant(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Advances this plant's age, performs its highest-priority successful action,
     * and prints the result with the plant's location when an action occurs.
     *
     * @param location map location occupied by this plant
     */
    @Override
    public final void tick(Location location) {
        age++;
        String result = takeTurn(location, age);
        if (result != null) {
            System.out.println(toString() + " at (" + location.x() + ", " + location.y()
                    + ") on " + location.map() + " " + result);
        }
    }

    /**
     * Attempts this plant's autonomous actions in priority order for the current age.
     * Implementations return after the first action that changes the game world.
     *
     * @param location location occupied by this plant
     * @param age current age of this plant in turns
     * @return a description of the action performed, or {@code null} if nothing happened
     */
    protected abstract String takeTurn(Location location, int age);

    /**
     * Checks whether an age is an exact multiple of an action interval.
     *
     * @param currentAge plant age in turns
     * @param interval positive number of turns between attempts
     * @return {@code true} when the action is due at this age
     */
    protected boolean due(int currentAge, int interval) {
        return currentAge % interval == 0;
    }

    /**
     * Performs a probability check using this plant's random source.
     *
     * @param probability success probability from zero to one
     * @return {@code true} when the probability check succeeds
     */
    protected boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    /**
     * Replaces this plant with a newly created next-stage plant when growth is due
     * and the probability check succeeds.
     *
     * @param location location whose ground will be replaced
     * @param currentAge current age of this plant
     * @param interval number of turns between growth attempts
     * @param probability probability that a due growth attempt succeeds
     * @param nextStage factory for the replacement plant
     * @return {@code true} if the location was changed to the next stage
     */
    protected boolean grow(Location location, int currentAge, int interval,
                           double probability, Supplier<? extends Plant> nextStage) {
        if (!due(currentAge, interval) || !chance(probability)) return false;
        location.setGround(nextStage.get());
        return true;
    }

    /**
     * Places a child plant on a random adjacent unoccupied plantable ground when due.
     *
     * @param location location of the parent plant
     * @param child new plant instance to place if a suitable location exists
     * @param currentAge current age of the parent plant
     * @param interval number of turns between spread attempts
     * @return location receiving the child, or {@code null} if spreading did not occur
     */
    protected Location spread(Location location, Plant child, int currentAge, int interval) {
        if (!due(currentAge, interval)) return null;
        List<Location> soil = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGroundAs(PlantableGround.class) != null
                    && adjacent.getGroundAs(Plant.class) == null) {
                soil.add(adjacent);
            }
        }
        if (soil.isEmpty()) return null;
        Location target = soil.get(random.nextInt(soil.size()));
        target.setGround(child);
        return target;
    }

    /**
     * Selects one random adjacent map location.
     *
     * @param location location whose exits provide adjacent tiles
     * @return a random adjacent location, or {@code null} if there are no exits
     */
    protected Location randomAdjacent(Location location) {
        List<Exit> exits = location.getExits();
        return exits.isEmpty() ? null : exits.get(random.nextInt(exits.size())).getDestination();
    }

    /**
     * Builds the location-aware message for a successful spread action.
     *
     * @param childName display name of the plant that was placed
     * @param target location where the child plant was placed
     * @return action description for the map log
     */
    protected String spreadMessage(String childName, Location target) {
        return "spreads, and a " + childName + " takes root at (" + target.x() + ", "
                + target.y() + ") on " + target.map() + ".";
    }

    /**
     * Drops one newly created produce item on a random adjacent tile.
     *
     * @param location location of the producing plant
     * @param itemName display name used in the action message
     * @param item factory for the produce item
     * @return action description, or {@code null} if no adjacent tile exists
     */
    protected String produce(Location location, String itemName, Supplier<edu.monash.fit2099.engine.items.Item> item) {
        Location target = randomAdjacent(location);
        if (target == null) return null;
        target.addItem(item.get());
        return "drops a " + itemName + " at (" + target.x() + ", " + target.y() + ") on " + target.map() + ".";
    }
}
