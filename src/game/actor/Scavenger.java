package game.actor;

import edu.monash.fit2099.engine.positions.Location;
import game.behaviour.ScavengerDutiesBehaviour;


/** A hostile NPC that steals loose resources and periodically lays snares. */
public class Scavenger extends HostileCreature {

    /** Registers scavenging as a turn duty before generic attacking/wandering. */
    public Scavenger() {
        super("Scavenger", 's', 90);
        addPriorityBehaviour(new ScavengerDutiesBehaviour());
    }

    /** @return the scavenger's attack damage */
    @Override
    protected int damage() { return 12; }

    /** @return the scavenger's percentage hit chance */
    @Override
    protected int hitChance() { return 65; }

    /** Drops stolen items when defeated. */
    @Override
    public void onAttacked(Location location, boolean killed) {
        if (!killed) return;
        PortableResourceTransfer.dropAll(this, location);
    }
}
