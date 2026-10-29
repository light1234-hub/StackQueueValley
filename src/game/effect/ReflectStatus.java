package game.effect;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.HostileCreature;

import java.util.ArrayList;
import java.util.List;

/**
 * Reflect status (Aegis Elixir). While active the owner takes no damage and the
 * blocked damage is dealt to an adjacent hostile creature instead.
 * <p>
 * The engine does not tell a status who attacked, so the blocked damage goes to
 * the adjacent hostile creatures in turn (round-robin).
 */
public class ReflectStatus implements Status {
    private final Actor owner;
    private Location lastKnown;
    private int turns;
    private int next;

    private ReflectStatus(Actor owner, Location location, int turns) {
        this.owner = owner;
        this.lastKnown = location;
        this.turns = turns;
    }

    /**
     * Applies (or refreshes) the reflect ward on an actor.
     *
     * @param owner actor to protect
     * @param location owner's current location
     * @param turns duration in turns
     */
    public static void applyTo(Actor owner, Location location, int turns) {
        for (ReflectStatus old : owner.statusesOf(ReflectStatus.class)) {
            owner.removeStatus(old);
        }
        ReflectStatus status = new ReflectStatus(owner, location, turns);
        owner.addStatus(status);
        ReflectingHealth health = new ReflectingHealth(
                owner.getMaximumStatistic(ActorStatistics.HEALTH),
                owner.getStatistic(ActorStatistics.HEALTH));
        health.setStatus(status);
        owner.addNewStatistic(ActorStatistics.HEALTH, health);
    }

    /** Sends blocked damage back to an adjacent hostile creature. */
    void reflect(int blocked) {
        GameMap map = lastKnown.map();
        if (!map.contains(owner)) {
            return;
        }
        List<Actor> attackers = new ArrayList<>();
        for (Exit exit : map.locationOf(owner).getExits()) {
            Actor other = exit.getDestination().getActor();
            if (other != null && other.asCapability(HostileCreature.class).isPresent()) {
                attackers.add(other);
            }
        }
        if (attackers.isEmpty()) {
            return;
        }
        Actor attacker = attackers.get(next++ % attackers.size());
        attacker.hurt(blocked);
        if (!attacker.isConscious()) {
            attacker.unconscious(owner, map);
        }
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        lastKnown = location;
        if (turns > 0) {
            turns--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return turns > 0;
    }
}