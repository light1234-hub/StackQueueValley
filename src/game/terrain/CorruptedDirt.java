package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TerrainAbility;
import game.actor.ZombieTarget;
import game.effect.PoisonEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;;


public class CorruptedDirt extends Ground {
    private static final int LIFETIME_TURNS = 10;
    private static final int SPREAD_INTERVAL = 2;
    private static final int MAX_SPREADS = 2;

    private int turnsLeft = LIFETIME_TURNS;
    private int spreadsMade = 0;
    private boolean firstTick = true;

    /**
     * Creates a corrupted dirt tile.
     */
    public CorruptedDirt() {
        super('x', "Corrupted dirt");
    }


    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor.hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
                actor.addStatus(new PoisonEffect(actor)); // check this matches how you attach statuses elsewhere
                location.setGround(new Dirt());
                return;
            }
        }
        // Prevent new infections from spreading immediately.
        if (firstTick) {
            firstTick = false;
            return;
        }

        turnsLeft--;
        if (turnsLeft <= 0) {
            location.setGround(new Dirt());
        }

        if (turnsLeft % SPREAD_INTERVAL == 0
                && spreadsMade < MAX_SPREADS) {
            spread(location);
        }
    }
    /**
     * Attempts to infect one randomly selected neighbouring tile.
     * Only unoccupied ground with the required corruption ability
     * can become corrupted.
     *
     * @param location the location spreading the infection
     */
    private void spread(Location location) {

        List<Exit> neighbours = new ArrayList<>(location.getExits());
        Collections.shuffle(neighbours);

        for (Exit exit : neighbours) {
            Location target = exit.getDestination();

            if (!target.containsAnActor()
                    && target.getGround().hasAbility(
                    TerrainAbility.CAN_BE_CORRUPTED)) {

                target.setGround(new CorruptedDirt());
                spreadsMade++;
                return;
            }
        }
    }
}