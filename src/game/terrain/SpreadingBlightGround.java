package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.effect.PoisonEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Temporary blighted ground that infects compatible neighbouring terrain and
 * poisons actors standing on it. Each patch has a limited lifetime and spread count.
 */
public class SpreadingBlightGround extends Ground {
    private static final int LIFETIME_TURNS = 10;
    private static final int SPREAD_INTERVAL = 2;
    private static final int MAX_SPREADS = 2;

    private int turns;
    private int spreads;

    /** Creates a blight patch and enables its environmental capabilities. */
    public SpreadingBlightGround() {
        super('B', "Spreading Blight");
        enableAbility(GroundAbility.GROVE_PURIFIABLE);
        enableAbility(GroundAbility.BLIGHT_INFECTABLE);
        enableAbility(GroundAbility.COLLAPSE_REPLACEABLE);
        enableAbility(GroundAbility.SURVIVOR_REPAIRABLE);
    }

    /**
     * Poisons an actor on this tile, spreads to a compatible neighbour at intervals,
     * and removes the patch when its lifetime expires.
     *
     * @param location location occupied by this blight patch
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            boolean alreadyPoisoned = actor.statusesOf(PoisonEffect.class).stream()
                    .anyMatch(PoisonEffect::isStatusActive);
            if (!alreadyPoisoned) {
                actor.addStatus(new PoisonEffect(actor));
            }
        }

        turns++;
        if (turns % SPREAD_INTERVAL == 0 && spreads < MAX_SPREADS) {
            spreadToOneNeighbour(location);
        }

        if (turns >= LIFETIME_TURNS && location.getGround() == this) {
            location.setGround(new Dirt());
        }
    }

    /**
     * Infects one random available neighbour that advertises blight susceptibility.
     *
     * @param location location occupied by this blight patch
     */
    private void spreadToOneNeighbour(Location location) {
        List<Exit> exits = new ArrayList<>(location.getExits());
        Collections.shuffle(exits);
        for (Exit exit : exits) {
            Location target = exit.getDestination();
            if (!target.containsAnActor()
                    && target.getGround().hasAbility(GroundAbility.BLIGHT_INFECTABLE)) {
                target.setGround(new SpreadingBlightGround());
                spreads++;
                return;
            }
        }
    }
}
