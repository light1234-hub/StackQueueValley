package game.behaviour;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.farmeraction.CreatureAttackAction;

import java.util.Objects;
import java.util.function.IntSupplier;

/**
 * Reusable combat decision: attack the first adjacent actor with the target
 * ability. Returns null if no target is adjacent, so the NPC can wander or
 * perform another lower-priority behaviour instead.
 */
public class AdjacentAttackBehaviour implements Behaviour<Actor, Action> {
    private final Enum<?> targetAbility;
    private final IntSupplier damage;
    private final IntSupplier hitChance;

    /** Creates an attack decision with live combat stats and a target ability. */
    public AdjacentAttackBehaviour(Enum<?> targetAbility, IntSupplier damage, IntSupplier hitChance) {
        this.targetAbility = Objects.requireNonNull(targetAbility);
        this.damage = Objects.requireNonNull(damage);
        this.hitChance = Objects.requireNonNull(hitChance);
    }

    /** Selects a nearby target or permits the next behaviour to act. */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location next = exit.getDestination();
            if (next.containsAnActor() && next.getActor().hasAbility(targetAbility)) {
                return new CreatureAttackAction(next.getActor(), exit.getName(),
                        damage.getAsInt(), hitChance.getAsInt());
            }
        }
        return null;
    }
}
