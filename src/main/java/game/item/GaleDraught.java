package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.CreatureControl;
import game.actor.HostileCreature;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gale Draught: every actor within radius 3 is pushed up to 3 tiles directly away
 * from the drinker (stopping early at obstacles). Hostile creatures in range are
 * stunned for 2 turns.
 */
public class GaleDraught extends Potion {
    private static final int RADIUS = 3;
    private static final int PUSH_DISTANCE = 3;
    private static final int STUN_TURNS = 2;

    public GaleDraught() {
        super("Gale Draught", '!', recipe(), 50, 3);
    }

    private static Map<Class<? extends Item>, Integer> recipe() {
        Map<Class<? extends Item>, Integer> recipe = new LinkedHashMap<>();
        recipe.put(StrawberryItem.class, 3);
        recipe.put(TomatoItem.class, 1);
        recipe.put(CopperItem.class, 1);
        return recipe;
    }

    private static int distance(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    @Override
    public String drink(Actor drinker, Location location) {
        GameMap map = location.map();
        List<Location> inRange = new ArrayList<>();
        for (Location nearby : location.getNearbyLocations(RADIUS)) {
            if (nearby.containsAnActor()) {
                inRange.add(nearby);
            }
        }
        // Farthest first, so nearer actors are not blocked by actors that are about to move.
        inRange.sort((a, b) -> Integer.compare(distance(location, b), distance(location, a)));

        int pushed = 0;
        int stunned = 0;
        for (Location start : inRange) {
            Actor target = start.getActor();
            int stepX = Integer.signum(start.x() - location.x());
            int stepY = Integer.signum(start.y() - location.y());
            Location current = start;
            for (int i = 0; i < PUSH_DISTANCE; i++) {
                int nx = current.x() + stepX;
                int ny = current.y() + stepY;
                if (!map.getXRange().contains(nx) || !map.getYRange().contains(ny)) {
                    break;
                }
                Location next = map.at(nx, ny);
                if (!next.canActorEnter(target)) {
                    break;
                }
                map.moveActor(target, next);
                current = next;
            }
            pushed++;
            if (target.asCapability(HostileCreature.class).isPresent()) {
                CreatureControl.stun(target.asCapability(HostileCreature.class).get(), map, STUN_TURNS);
                stunned++;
            }
        }
        return drinker + " drinks the Gale Draught: " + pushed + " actor(s) blown back, "
                + stunned + " hostile creature(s) stunned.";
    }
}