package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.ZombieTarget;

/**
 * A temporary, single-use poison trap attached to a ground tile. It preserves
 * the tile's terrain and poisons the first Zombie target that steps onto it.
 */
public class PoisonTrap implements Status {
    private static final int LIFETIME_TURNS = 10;
    private int turnsRemaining = LIFETIME_TURNS;
    private boolean armed = true;

    /**
     * Triggers on a Zombie target occupying the trapped tile and expires after
     * triggering or after its lifetime ends.
     *
     * @param currEntity the ground entity carrying this trap
     * @param location the tile carrying the trap
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (!armed || turnsRemaining <= 0) {
            return;
        }

        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor.hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
                boolean alreadyPoisoned = actor.statusesOf(PoisonEffect.class).stream()
                        .anyMatch(PoisonEffect::isStatusActive);
                if (!alreadyPoisoned) {
                    actor.addStatus(new PoisonEffect(actor));
                }
                armed = false;
                turnsRemaining = 0;
                return;
            }
        }

        turnsRemaining--;
    }

    /** @return true while this trap is armed and has time remaining */
    @Override
    public boolean isStatusActive() {
        return armed && turnsRemaining > 0;
    }
}
