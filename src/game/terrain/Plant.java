package game.terrain;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Shared turn-based lifecycle and common growth operations for plants in the valley.
 * Plant instances advertise capabilities used by environmental effects such as blight,
 * snares and cave collapses.
 */
public abstract class Plant extends Ground {
    private int age;
    private final Random random = new Random();

    /**
     * Creates a plant with the specified display character and name.
     *
     * @param displayChar map character representing this plant
     * @param name display name of this plant
     */
    protected Plant(char displayChar, String name) {
        super(displayChar, name);
        enableAbility(GroundAbility.BLIGHT_INFECTABLE);
        enableAbility(GroundAbility.TRAP_BUILDABLE);
        enableAbility(GroundAbility.COLLAPSE_REPLACEABLE);
    }

    /**
     * Advances this plant's age and performs the highest-priority action available.
     *
     * @param location map location containing this plant
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
     * Performs this plant's turn-specific actions in priority order.
     *
     * @param location location containing this plant
     * @param age current age of this plant in turns
     * @return a message describing the action, or {@code null} if nothing happened
     */
    protected abstract String takeTurn(Location location, int age);

    /**
     * Checks whether the current age is a multiple of an interval.
     *
     * @param currentAge current plant age
     * @param interval positive turn interval
     * @return whether the interval is due
     */
    protected boolean due(int currentAge, int interval) {
        return interval > 0 && currentAge % interval == 0;
    }

    /**
     * Returns whether an event occurs for the supplied probability.
     *
     * @param probability event probability from zero to one
     * @return whether the random sample is below the probability
     */
    protected boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    /**
     * Replaces this plant with a new growth stage when its interval and probability match.
     *
     * @param location location containing this plant
     * @param currentAge current plant age
     * @param interval growth interval
     * @param probability chance of growth on a due turn
     * @param nextStage factory that creates the next plant stage
     * @return whether the plant grew
     */
    protected boolean grow(Location location, int currentAge, int interval,
                           double probability, Supplier<? extends Plant> nextStage) {
        if (!due(currentAge, interval) || !chance(probability)) {
            return false;
        }
        location.setGround(nextStage.get());
        return true;
    }

    /**
     * Spreads a child plant to a random adjacent plantable tile when due.
     *
     * @param location location containing this plant
     * @param child plant to place on the chosen tile
     * @param currentAge current plant age
     * @param interval spreading interval
     * @return the new plant's location, or {@code null} if no spread occurred
     */
    protected Location spread(Location location, Plant child, int currentAge, int interval) {
        if (!due(currentAge, interval)) {
            return null;
        }
        List<Location> soil = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGroundAs(PlantableGround.class) != null
                    && adjacent.getGroundAs(Plant.class) == null) {
                soil.add(adjacent);
            }
        }
        if (soil.isEmpty()) {
            return null;
        }
        Location target = soil.get(random.nextInt(soil.size()));
        target.setGround(child);
        return target;
    }

    /**
     * Selects a random tile adjacent to this plant.
     *
     * @param location location containing this plant
     * @return a random adjacent location, or {@code null} when there are no exits
     */
    protected Location randomAdjacent(Location location) {
        List<Exit> exits = location.getExits();
        return exits.isEmpty() ? null : exits.get(random.nextInt(exits.size())).getDestination();
    }

    /**
     * Builds a standard message for a successful spreading action.
     *
     * @param childName display name of the new plant
     * @param target location where the new plant was placed
     * @return the spreading message
     */
    protected String spreadMessage(String childName, Location target) {
        return "spreads, and a " + childName + " takes root at (" + target.x() + ", "
                + target.y() + ") on " + target.map() + ".";
    }

    /**
     * Drops a produced item on a randomly selected adjacent tile.
     *
     * @param location location containing this plant
     * @param itemName display name for the produced item
     * @param item factory that creates the item
     * @return a production message, or {@code null} if no adjacent tile exists
     */
    protected String produce(Location location, String itemName, Supplier<Item> item) {
        Location target = randomAdjacent(location);
        if (target == null) {
            return null;
        }
        target.addItem(item.get());
        return "drops a " + itemName + " at (" + target.x() + ", " + target.y()
                + ") on " + target.map() + ".";
    }
}
