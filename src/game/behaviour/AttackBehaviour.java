package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.ZombieTarget;
import game.farmeraction.CreatureAttackAction;

import java.util.function.IntSupplier;

/** Allows a hostile creature to attack an adjacent valid target. */
public class AttackBehaviour implements Behaviour<Actor, Action> {
    private final IntSupplier damage;
    private final IntSupplier hitChance;

    /**
     * Creates an attack behaviour.
     *
     * @param damage supplier of the creature's attack damage
     * @param hitChance supplier of the creature's hit chance
     */
    public AttackBehaviour(IntSupplier damage, IntSupplier hitChance) {
        this.damage = damage;
        this.hitChance = hitChance;
    }

    /**
     * Selects an adjacent target to attack.
     *
     * @param actor the attacking creature
     * @param location the creature's current location
     * @return an attack action, or {@code null} if no target is adjacent
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location next = exit.getDestination();
            if (next.containsAnActor()
                    && next.getActor().hasAbility(ZombieTarget.ZOMBIE_TARGET)) {
                return new CreatureAttackAction(next.getActor(), exit.getName(),
                        damage.getAsInt(), hitChance.getAsInt());
            }
        }
        return null;
    }
}
