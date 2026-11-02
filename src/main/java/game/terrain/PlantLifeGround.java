package game.terrain;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Shared age, tick-order, growth and spreading mechanics for plant grounds. */
abstract class PlantLifeGround extends Ground {
    protected final Random random = new Random();
    protected int age;
    private boolean skipNextTick;

    protected PlantLifeGround(char displayChar, String name) {
        super(displayChar, name);
    }

    /** @return number of world turns this plant has aged */
    public final int age() { return age; }

    /** Advances this plant by one world tick and reports only a successful event. */
    @Override
    public final void tick(Location location) {
        if (skipNextTick) {
            skipNextTick = false;
            return;
        }
        age++;
        String result = takeTurn(location);
        if (result != null) {
            System.out.println(this + " at (" + location.x() + ", " + location.y()
                    + ") on " + location.map() + " " + result);
        }
    }

    /** Performs at most one lifecycle event during this tick. */
    protected abstract String takeTurn(Location location);

    protected final boolean due(int interval) { return age % interval == 0; }

    /** Replaces this ground with its next stage when the interval and chance allow it. */
    protected final boolean growInto(Location location, int interval, double probability,
                                     PlantLifeGround nextStage) {
        if (!due(interval) || random.nextDouble() >= probability) return false;
        location.setGround(nextStage);
        return true;
    }

    /** Spreads a new plant to one adjacent fertile location when the interval is due. */
    protected final Location spread(Location location, PlantLifeGround child, int interval) {
        if (!due(interval)) return null;
        List<Location> fertileLocations = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround().hasAbility(GroundAbility.FERTILE)) fertileLocations.add(adjacent);
        }
        if (fertileLocations.isEmpty()) return null;
        Location target = fertileLocations.get(random.nextInt(fertileLocations.size()));
        if (comesAfterInMapTickOrder(location, target)) child.deferFirstTick();
        target.setGround(child);
        return target;
    }

    protected final Location randomAdjacent(Location location) {
        List<Exit> exits = location.getExits();
        return exits.isEmpty() ? null : exits.get(random.nextInt(exits.size())).getDestination();
    }

    protected final String spreadMessage(String childName, Location target) {
        return "spreads, and a " + childName + " takes root at (" + target.x() + ", " + target.y()
                + ") on " + target.map() + ".";
    }

    private void deferFirstTick() { skipNextTick = true; }

    private boolean comesAfterInMapTickOrder(Location source, Location target) {
        return target.y() > source.y() || (target.y() == source.y() && target.x() > source.x());
    }
}
