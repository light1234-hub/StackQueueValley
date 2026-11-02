
package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A persistent magical grove that gradually purifies nearby corrupted ground.
 * Soil restored by the grove can later grow a strawberry seedling.
 */
public class LivingGroveGround extends Ground {
    private static final int EFFECT_INTERVAL = 2;
    private static final int RADIUS = 2;

    private int turns;
    private final List<Location> restoredPlots = new ArrayList<>();

    /** Creates a grove that acts on nearby terrain every two world turns. */
    public LivingGroveGround() {
        super('G', "Living Grove");
    }

    /**
     * Advances the grove's cycle, grows previously restored plots, and purifies
     * or enriches one nearby suitable tile.
     *
     * @param location location occupied by this grove
     */
    @Override
    public void tick(Location location) {
        turns++;

        if (turns % EFFECT_INTERVAL != 0) {
            return;
        }

        if (growPreviouslyRestoredPlot()) {
            return;
        }

        if (restoreNearbyGround(location, GroundAbility.GROVE_PURIFIABLE)) {
            return;
        }

        restoreNearbyGround(location, GroundAbility.GROVE_ENRICHABLE);
    }

    /**
     * Grows a seedling on a clear soil plot restored during an earlier turn.
     *
     * @return whether a seedling was planted
     */
    private boolean growPreviouslyRestoredPlot() {
        Iterator<Location> iterator = restoredPlots.iterator();

        while (iterator.hasNext()) {
            Location plot = iterator.next();

            if (plot.getGround().hasAbility(GroundAbility.GROVE_SEEDING_PLOT)
                    && !plot.containsAnActor()) {
                plot.setGround(new StrawberrySeedling());
                iterator.remove();
                return true;
            }

            if (!plot.getGround().hasAbility(GroundAbility.GROVE_SEEDING_PLOT)) {
                iterator.remove();
            }
        }

        return false;
    }

    /**
     * Restores one unoccupied nearby tile with the specified ability.
     *
     * @param location location occupied by this grove
     * @param ability ability required for restoration
     * @return whether a tile was restored
     */
    private boolean restoreNearbyGround(Location location, GroundAbility ability) {
        for (Location target : nearbyLocations(location)) {
            if (!target.containsAnActor()
                    && target.getGround().hasAbility(ability)) {
                target.setGround(new Soil());
                restoredPlots.add(target);
                return true;
            }
        }

        return false;
    }

    /**
     * Returns locations in the grove's Manhattan-distance radius,
     * excluding the grove itself.
     *
     * @param location location occupied by this grove
     * @return nearby locations ordered by row then column
     */
    private List<Location> nearbyLocations(Location location) {
        List<Location> result = new ArrayList<>();

        for (int y = Math.max(location.map().getYRange().min(), location.y() - RADIUS);
             y <= Math.min(location.map().getYRange().max(), location.y() + RADIUS); y++) {

            for (int x = Math.max(location.map().getXRange().min(), location.x() - RADIUS);
                 x <= Math.min(location.map().getXRange().max(), location.x() + RADIUS); x++) {

                if (Math.abs(x - location.x()) + Math.abs(y - location.y()) <= RADIUS
                        && (x != location.x() || y != location.y())) {
                    result.add(location.map().at(x, y));
                }
            }
        }

        return result;
    }
}

