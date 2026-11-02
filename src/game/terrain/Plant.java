package game.terrain;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/** Shared turn-based lifecycle for every plant in the valley. */
public abstract class Plant extends Ground {
    private int age;
    private final Random random = new Random();

    protected Plant(char displayChar, String name) {
        super(displayChar, name);
    }

    @Override
    public final void tick(Location location) {
        age++;
        String result = takeTurn(location, age);
        if (result != null) {
            System.out.println(toString() + " at (" + location.x() + ", " + location.y()
                    + ") on " + location.map() + " " + result);
        }
    }

    /** Tries this plant's actions in priority order; return null when nothing happens. */
    protected abstract String takeTurn(Location location, int age);

    protected boolean due(int currentAge, int interval) {
        return currentAge % interval == 0;
    }

    protected boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    protected boolean grow(Location location, int currentAge, int interval,
                           double probability, Supplier<? extends Plant> nextStage) {
        if (!due(currentAge, interval) || !chance(probability)) return false;
        location.setGround(nextStage.get());
        return true;
    }

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

    protected Location randomAdjacent(Location location) {
        List<Exit> exits = location.getExits();
        return exits.isEmpty() ? null : exits.get(random.nextInt(exits.size())).getDestination();
    }

    protected String spreadMessage(String childName, Location target) {
        return "spreads, and a " + childName + " takes root at (" + target.x() + ", "
                + target.y() + ") on " + target.map() + ".";
    }

    protected String produce(Location location, String itemName, Supplier<edu.monash.fit2099.engine.items.Item> item) {
        Location target = randomAdjacent(location);
        if (target == null) return null;
        target.addItem(item.get());
        return "drops a " + itemName + " at (" + target.x() + ", " + target.y() + ") on " + target.map() + ".";
    }
}
