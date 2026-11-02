package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ground that collapses after a countdown. The collapse affects an area around
 * the unstable tile, displaces nearby actors when possible, scatters loose items,
 * and converts replaceable terrain into blocking rocks.
 */
public class UnstableGround extends Ground {
    private static final int COUNTDOWN_TURNS = 3;
    private static final int COLLAPSE_DAMAGE = 20;

    private int turnsRemaining = COUNTDOWN_TURNS;

    /** Creates unstable terrain that collapses after three world turns. */
    public UnstableGround() {
        super('!', "Unstable Ground");
        enableAbility(GroundAbility.SURVIVOR_REPAIRABLE);
    }

    /**
     * Advances the countdown and triggers the collapse when it expires.
     *
     * @param location location containing this unstable ground
     */
    @Override
    public void tick(Location location) {
        if (location.getGround() != this) {
            return;
        }
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            collapse(location);
        }
    }

    /**
     * Resolves the area collapse by moving or damaging actors, scattering items,
     * and replacing affected ordinary terrain with rock.
     *
     * @param centre location where the collapse begins
     */
    private void collapse(Location centre) {
        List<Location> affectedTiles = affectedArea(centre);
        Set<Location> affected = new HashSet<>(affectedTiles);

        for (Location tile : affectedTiles) {
            if (!tile.containsAnActor()) {
                continue;
            }
            Actor actor = tile.getActor();
            Location safe = findSafeDestination(affected, actor);
            if (safe != null) {
                tile.map().moveActor(actor, safe);
            } else {
                actor.hurt(COLLAPSE_DAMAGE);
            }
        }

        for (Location tile : affectedTiles) {
            scatterItems(tile, affected);
        }

        for (Location tile : affectedTiles) {
            // The centre always collapses; neighbouring unstable tiles retain their own timers.
            if (tile == centre
                    || tile.getGround().hasAbility(GroundAbility.COLLAPSE_REPLACEABLE)) {
                tile.setGround(new Rock());
            }
        }
    }

    /**
     * Builds the collapse area from the centre tile and its adjacent locations.
     *
     * @param centre centre of the collapse
     * @return unique affected locations
     */
    private List<Location> affectedArea(Location centre) {
        List<Location> area = new ArrayList<>();
        area.add(centre);
        for (Exit exit : centre.getExits()) {
            Location neighbour = exit.getDestination();
            if (!area.contains(neighbour)) {
                area.add(neighbour);
            }
        }
        return area;
    }

    /**
     * Finds an unoccupied, enterable tile just outside the collapse area.
     *
     * @param affected locations inside the collapse area
     * @param actor actor that needs to escape
     * @return a safe destination, or {@code null} if none is available
     */
    private Location findSafeDestination(Set<Location> affected, Actor actor) {
        for (Location boundary : affected) {
            for (Exit exit : boundary.getExits()) {
                Location candidate = exit.getDestination();
                if (!affected.contains(candidate)
                        && !candidate.containsAnActor()
                        && candidate.canActorEnter(actor)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Moves loose items to a free location outside the collapse area where possible.
     * Items remain on the original tile if no destination is available.
     *
     * @param source tile whose items are being scattered
     * @param affected locations inside the collapse area
     */
    private void scatterItems(Location source, Set<Location> affected) {
        for (Item item : new ArrayList<>(source.getItems())) {
            Location destination = findItemDestination(affected);
            if (destination != null) {
                source.removeItem(item);
                destination.addItem(item);
            }
        }
    }

    /**
     * Finds a tile outside the collapse area with no actor on it.
     *
     * @param affected locations inside the collapse area
     * @return a destination, or {@code null} if no free destination exists
     */
    private Location findItemDestination(Set<Location> affected) {
        for (Location boundary : affected) {
            for (Exit exit : boundary.getExits()) {
                Location candidate = exit.getDestination();
                if (!affected.contains(candidate) && !candidate.containsAnActor()) {
                    return candidate;
                }
            }
        }
        return null;
    }
}
