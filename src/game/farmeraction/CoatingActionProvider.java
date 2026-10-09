package game.farmeraction;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Coatable;
import game.blueprint.CoatingSource;
import game.blueprint.CoatingStatus;

import java.util.ArrayList;
import java.util.List;

/** Discovers coating sources and builds actions for carried coatable items. */
public class CoatingActionProvider {

    /**
     * Adds coating actions for sources in the actor's inventory or on adjacent
     * tiles. The actor's current tile is intentionally not treated as adjacent.
     *
     * @param actor actor receiving the actions
     * @param map map containing the actor
     * @param actions action list to update
     */
    public void addAvailableActions(Actor actor, GameMap map, ActionList actions) {
        List<CoatingSourceReference> sources = findSources(actor, map);
        for (Item item : actor.getInventory().getItems()) {
            Coatable target = item.asCapability(Coatable.class).orElse(null);
            if (target == null) continue;
            for (CoatingSourceReference reference : sources) {
                if (reference.item == item) continue;
                actions.add(new ApplyCoatingAction(item, target, reference.source,
                        reference.item, reference.location));
            }
        }
    }

    /** Finds carried and adjacent coating sources through their capabilities. */
    private List<CoatingSourceReference> findSources(Actor actor, GameMap map) {
        List<CoatingSourceReference> sources = new ArrayList<>();
        for (Item item : actor.getInventory().getItems()) {
            item.asCapability(CoatingSource.class).ifPresent(
                    source -> sources.add(new CoatingSourceReference(source, item, null)));
        }

        Location current = map.locationOf(actor);
        for (Exit exit : current.getExits()) {
            Location adjacent = exit.getDestination();
            for (Item item : adjacent.getItems()) {
                item.asCapability(CoatingSource.class).ifPresent(
                        source -> sources.add(new CoatingSourceReference(source, item, adjacent)));
            }
            CoatingSource groundSource = adjacent.getGroundAs(CoatingSource.class);
            if (groundSource != null) {
                sources.add(new CoatingSourceReference(groundSource, null, adjacent));
            }
            for (CoatingStatus status : adjacent.getGround().statusesOf(CoatingStatus.class)) {
                if (status.isAvailable()) {
                    sources.add(new CoatingSourceReference(status, null, adjacent));
                }
            }
        }
        return sources;
    }

    /** Retains the source and the location from which a consumable is removed. */
    private static class CoatingSourceReference {
        private final CoatingSource source;
        private final Item item;
        private final Location location;

        private CoatingSourceReference(CoatingSource source, Item item, Location location) {
            this.source = source;
            this.item = item;
            this.location = location;
        }
    }
}
