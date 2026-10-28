package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.GroundAbility;
import game.ability.TerrainAbility;
import game.actor.ZombieTarget;
import game.effect.PoisonEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Temporary corrupted terrain that can spread to compatible neighbouring ground.
 * It is vulnerable to purification and repair from the REQ3 environmental consequences.
 */
public class CorruptedDirt extends Ground {
    private static final int LIFETIME_TURNS = 10;
    private static final int SPREAD_INTERVAL = 2;
    private static final int MAX_SPREADS = 2;

    private int turnsLeft = LIFETIME_TURNS;
    private int spreadsMade;
    private boolean firstTick = true;

    /** Creates a corrupted tile and advertises its terrain capabilities. */
    public CorruptedDirt() {
        super('x', "Corrupted dirt");
        enableAbility(TerrainAbility.CAN_BE_CORRUPTED);
        enableAbility(GroundAbility.GROVE_PURIFIABLE);
        enableAbility(GroundAbility.BLIGHT_INFECTABLE);
        enableAbility(GroundAbility.COLLAPSE_REPLACEABLE);
        enableAbility(GroundAbility.SURVIVOR_REPAIRABLE);
        enableAbility(GroundAbility.QUEST_FEATURE_PLACEABLE);
    }

    /**
     * Advances the corruption lifetime, poisons targeted creatures, and spreads infection.
     *
     * @param location location containing this corrupted tile
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor.hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
                actor.addStatus(new PoisonEffect(actor));
                location.setGround(new Dirt());
                return;
            }
        }
        if (firstTick) {
            firstTick = false;
            return;
        }

        turnsLeft--;
        if (turnsLeft <= 0) {
            location.setGround(new Dirt());
            return;
        }

        if (turnsLeft % SPREAD_INTERVAL == 0 && spreadsMade < MAX_SPREADS) {
            spread(location);
        }
    }

    /**
     * Attempts to corrupt one randomly ordered compatible neighbouring tile.
     *
     * @param location corrupted tile that is spreading
     */
    private void spread(Location location) {
        List<Exit> neighbours = new ArrayList<>(location.getExits());
        Collections.shuffle(neighbours);

        for (Exit exit : neighbours) {
            Location target = exit.getDestination();
            if (!target.containsAnActor()
                    && target.getGround().hasAbility(TerrainAbility.CAN_BE_CORRUPTED)) {
                target.setGround(new CorruptedDirt());
                spreadsMade++;
                return;
            }
        }
    }
}