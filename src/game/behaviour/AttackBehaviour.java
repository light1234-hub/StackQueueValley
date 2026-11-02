package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.ZombieTarget;
import game.farmeraction.CreatureAttackAction;

import java.util.function.IntSupplier;

/** Selects an attack action when a zombie target is adjacent. */
public final class AttackBehaviour implements Behaviour<Actor, Action> {
    private final IntSupplier damage;
    private final IntSupplier hitChance;

    /**
     * Creates the attack behavior.
     *
     * @param damage supplier for the creature's current damage
     * @param hitChance supplier for the creature's current hit chance
     */
    public AttackBehaviour(IntSupplier damage, IntSupplier hitChance) {
        this.damage = damage;
        this.hitChance = hitChance;
    }

    /**
     * Finds and attacks the first adjacent zombie target.
     *
     * @param actor creature performing the attack
     * @param location creature's current location
     * @return an attack action, or {@code null} when no target is adjacent
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location target = exit.getDestination();
            if (target.containsAnActor()
                    && target.getActor().hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
                return new CreatureAttackAction(
                        target.getActor(), exit.getName(), damage.getAsInt(), hitChance.getAsInt());
            }
        }
        return null;
    }
}
