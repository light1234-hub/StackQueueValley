package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.HostileCreature;

/** Planted by the Orchard Cauldron. Grows into an ArcaneTree after 3 turns, even after collection. */
public class ArcaneSapling extends Ground {
    private static final int GROW_TURNS = 3;
    private final Ground previous;
    private int age;

    /** @param previous ground to restore if the sapling is trampled */
    public ArcaneSapling(Ground previous) {
        super(',', "Arcane Sapling");
        this.previous = previous;
    }

    @Override
    public void tick(Location location) {
        Actor visitor = location.getActor();
        if (visitor != null) {
            if (visitor.asCapability(HostileCreature.class).isPresent()) {
                location.setGround(previous); // trampled
            }
            return; // anyone standing on it delays growth
        }
        if (++age >= GROW_TURNS) {
            location.setGround(new ArcaneTree());
        }
    }
}