package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A burning status effect attached directly to an actor (mirrors the
 * structure of PoisonEffect/HealingEffect), with configurable duration and
 * damage so it can represent both the teleportation-circle burn and the
 * geode-explosion burn.
 */
public class BurnEffect implements Status {
    private int duration;
    private final int damagePerTurn;
    private final Actor victim;

    /**
     * Creates a burn effect tied directly to the actor who caught fire.
     *
     * @param victim the actor who will take damage from this burn
     * @param duration number of turns the burn lasts
     * @param damagePerTurn damage dealt each turn
     */
    public BurnEffect(Actor victim, int duration, int damagePerTurn) {
        this.victim = victim;
        this.duration = duration;
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (duration > 0) {
            victim.hurt(damagePerTurn);
            duration--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return duration > 0;
    }
}
