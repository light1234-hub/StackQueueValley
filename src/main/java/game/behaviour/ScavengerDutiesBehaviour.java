package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.actor.PortableResourceTransfer;
import game.terrain.ScavengerTrapGround;

import java.util.ArrayList;
import java.util.List;

/**
 * A scavenger's resource-theft and snare-building brain. These are passive
 * turn duties: returning null intentionally allows its shared hostile attack
 * and wander behaviours to choose the actual turn action afterward.
 */
public class ScavengerDutiesBehaviour implements Behaviour<Actor, Action> {
    private static final int TRAP_INTERVAL_TURNS = 3;
    private static final int MAX_TRAPS = 3;
    private int turns;
    private int trapsBuilt;

    /** Steals portable ground items and periodically creates an adjacent trap. */
    @Override
    public Action operate(Actor actor, Location current) {
        PortableResourceTransfer.collect(actor, current);
        turns++;
        if (turns % TRAP_INTERVAL_TURNS == 0 && trapsBuilt < MAX_TRAPS) {
            buildTrap(actor, current);
        }
        return null;
    }

    /** Creates a snare on the first free, trap-buildable adjacent tile. */
    private void buildTrap(Actor actor, Location current) {
        List<Location> candidates = new ArrayList<>();
        for (Exit exit : current.getExits()) {
            Location target = exit.getDestination();
            Ground ground = target.getGround();
            if (!target.containsAnActor()
                    && target.canActorEnter(actor)
                    && ground.hasAbility(GroundAbility.TRAP_BUILDABLE)) {
                candidates.add(target);
            }
        }
        if (!candidates.isEmpty()) {
            Location target = candidates.get(0);
            target.setGround(new ScavengerTrapGround(target.getGround()));
            trapsBuilt++;
        }
    }
}