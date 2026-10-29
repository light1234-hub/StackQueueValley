package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.exceptions.GameEngineException;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.StoneGolem;
import game.item.ProspectorElixir;
import game.item.StoneItem;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Brews Prospector's Elixir. A StoneGolem is spawned at brew start. Each turn it takes one step
 * along the shortest path towards a random Rock; once beside it, it shatters the Rock, which
 * becomes Dirt with a Stone item left on that very tile. The farmer must pick the Stone up.
 * The golem crumbles when the potion is collected.
 */
public class QuarryCauldron extends BrewingStation {
    private static final Random RNG = new Random();
    private StoneGolem golem;
    private Location targetRock;

    public QuarryCauldron() {
        super('U', "Quarry Cauldron", ProspectorElixir::new);
    }

    @Override
    protected void onBrewStarted(Location location) {
        targetRock = null;
        StoneGolem candidate = new StoneGolem();
        for (Exit e : location.getExits()) {
            Location d = e.getDestination();
            if (d.canActorEnter(candidate)) {
                try {
                    location.map().addActor(candidate, d);
                    golem = candidate;
                    return;
                } catch (GameEngineException ex) {
                    golem = null;
                }
            }
        }
    }

    @Override
    protected void applyBrewEffect(Location location) {
        if (golem == null) {
            return;
        }
        GameMap map = location.map();
        if (targetRock == null || !TileFinder.isRock(targetRock)) {
            List<Location> rocks = TileFinder.rocks(map);
            if (rocks.isEmpty()) {
                return;
            }
            targetRock = rocks.get(RNG.nextInt(rocks.size()));
        }
        Location here = map.locationOf(golem);
        if (isNextTo(here, targetRock)) {
            targetRock.setGround(new Dirt());
            targetRock.addItem(new StoneItem());
            targetRock = null;
            return;
        }
        Location step = firstStepToward(here, targetRock);
        if (step != null) {
            map.moveActor(golem, step);
        } else {
            targetRock = null; // unreachable: pick another rock next turn
        }
    }

    @Override
    protected void onCollected(Actor collector, Location location) {
        if (golem != null) {
            location.map().removeActor(golem);
            golem = null;
        }
        targetRock = null;
    }

    private static boolean isNextTo(Location a, Location b) {
        for (Exit e : a.getExits()) {
            if (e.getDestination() == b) {
                return true;
            }
        }
        return false;
    }

    /** Breadth-first search over tiles the golem can enter; returns the first step of the shortest path. */
    private Location firstStepToward(Location start, Location rock) {
        Map<Location, Location> firstStep = new HashMap<>();
        Queue<Location> queue = new ArrayDeque<>();
        queue.add(start);
        firstStep.put(start, null);
        while (!queue.isEmpty()) {
            Location current = queue.poll();
            if (isNextTo(current, rock) && current != start) {
                return firstStep.get(current);
            }
            for (Exit e : current.getExits()) {
                Location next = e.getDestination();
                if (!firstStep.containsKey(next) && next.canActorEnter(golem)) {
                    firstStep.put(next, current == start ? next : firstStep.get(current));
                    queue.add(next);
                }
            }
        }
        return null;
    }
}