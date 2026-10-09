package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;


public class FireEffect implements Status {
    private int duration;
    private final int groundDamagePerTurn;
    private final int burnDuration;
    private final int burnDamagePerTurn;

    /**
     * Creates a fire effect on a piece of ground.
     *
     * @param duration how many turns the ground itself stays on fire
     * @param groundDamagePerTurn damage dealt directly to whoever is
     *                            standing on the fire each turn (0 if the
     *                            ground fire itself shouldn't deal damage,
     *                            only the resulting burn should)
     * @param burnDuration how many turns the burn effect lasts on an actor
     *                     who walks through the fire
     * @param burnDamagePerTurn damage dealt per turn by that burn effect
     */
    public FireEffect(int duration, int groundDamagePerTurn, int burnDuration, int burnDamagePerTurn) {
        this.duration = duration;
        this.groundDamagePerTurn = groundDamagePerTurn;
        this.burnDuration = burnDuration;
        this.burnDamagePerTurn = burnDamagePerTurn;
    }

    public boolean isActive() { return duration > 0; }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (duration > 0) {
            if (location.containsAnActor()) {
                Actor victim = location.getActor();
                if (groundDamagePerTurn > 0) {
                    victim.hurt(groundDamagePerTurn);
                }
                // Standing in fire re-applies the burn once the old one ends, but
                // burns must not stack or the damage would multiply every turn.
                boolean alreadyBurning = victim.statusesOf(BurnEffect.class).stream()
                        .anyMatch(BurnEffect::isStatusActive);
                if (!alreadyBurning) {
                    victim.addStatus(new BurnEffect(victim, burnDuration, burnDamagePerTurn));
                }
            }
            duration--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return isActive();
    }
}