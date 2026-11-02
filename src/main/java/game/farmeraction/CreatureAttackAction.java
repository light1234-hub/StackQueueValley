package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import java.util.Random;

/** Resolves a hostile creature's probabilistic attack against the farmer. */
public class CreatureAttackAction extends Action {
    private final Actor target;
    private final String direction;
    private final int damage, hitChance;
    /**
     * Creates an attack with the specified damage and hit chance.
     * @param target actor being attacked
     * @param direction direction of the attack, for display
     * @param damage damage dealt on a hit
     * @param hitChance percentage chance of hitting
     */
    public CreatureAttackAction(Actor target, String direction, int damage, int hitChance) {
        this.target = target; this.direction = direction; this.damage = damage; this.hitChance = hitChance;
    }
    /** Rolls the hit chance, applies damage, and removes an unconscious target. */
    @Override public String execute(Actor actor, GameMap map) {
        if (new Random().nextInt(100) >= hitChance) return actor + " misses " + target + ".";
        target.hurt(damage);
        String result = actor + " hits " + target + " for " + damage + " damage.";
        if (!target.isConscious()) result += "\n" + target.unconscious(actor, map);
        return result;
    }
    /** Describes the attack in the action menu. */
    @Override public String menuDescription(Actor actor) { return actor + " attacks " + target + " from " + direction; }
}
