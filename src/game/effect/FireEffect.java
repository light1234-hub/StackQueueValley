package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.CoatingSource;
import game.blueprint.Coating;

/**
 * Fire sitting on a piece of ground, exactly like ChargeEffect but for
 * flames instead of electricity. Attached to the Ground at a Location.
 * <p>
 * Two things happen each turn while active, matching REQ2's wording for
 * both the teleportation circle and the exploding geode:
 * <ol>
 *   <li>if groundDamagePerTurn &gt; 0, whoever is standing on the burning
 *   tile takes that much damage directly (used for the geode explosion's
 *   fire; the teleportation circle's fire deals 0 here since the spec only
 *   describes the walk-through burn for that case)</li>
 *   <li>whoever is standing there also catches a {@link BurnEffect}, which
 *   keeps damaging them for a number of turns even after they leave the
 *   tile</li>
 * </ol>
 */
public class FireEffect implements Status, CoatingSource {
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

    /** Creates the fire coating provided by active flames. */
    @Override
    public Coating createCoating() {
        return new FireCoating();
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (duration > 0) {
            if (location.containsAnActor()) {
                Actor victim = location.getActor();
                if (groundDamagePerTurn > 0) {
                    victim.hurt(groundDamagePerTurn);
                }
                victim.addStatus(new BurnEffect(victim, burnDuration, burnDamagePerTurn));
            }
            duration--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return isActive();
    }
}
