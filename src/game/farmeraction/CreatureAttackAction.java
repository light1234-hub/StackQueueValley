package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import java.util.Random;

/** A creature's probabilistic attack against the farmer. */
public class CreatureAttackAction extends Action {
    private final Actor target;
    private final String direction;
    private final int damage, hitChance;
    public CreatureAttackAction(Actor target, String direction, int damage, int hitChance) {
        this.target = target; this.direction = direction; this.damage = damage; this.hitChance = hitChance;
    }
    @Override public String execute(Actor actor, GameMap map) {
        if (new Random().nextInt(100) >= hitChance) return actor + " misses " + target + ".";
        target.hurt(damage);
        String result = actor + " hits " + target + " for " + damage + " damage.";
        if (!target.isConscious()) result += "\n" + target.unconscious(actor, map);
        return result;
    }
    @Override public String menuDescription(Actor actor) { return actor + " attacks " + target + " from " + direction; }
}
