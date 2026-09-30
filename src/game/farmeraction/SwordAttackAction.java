package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.HostileCreature;
import game.effect.BurnEffect;
import game.item.Sword;
import java.util.Random;

/** The farmer's 85% accurate sword attack, including the current coating. */
public class SwordAttackAction extends Action {
    private final HostileCreature target;
    private final String direction;
    private final Sword sword;
    public SwordAttackAction(HostileCreature target, String direction, Sword sword) {
        this.target = target; this.direction = direction; this.sword = sword;
    }
    @Override public String execute(Actor actor, GameMap map) {
        boolean hit = new Random().nextInt(100) < 85;
        String result;
        if (hit) { target.hurt(20); result = actor + " hits " + target + " with the sword for 20 damage."; }
        else result = actor + " misses " + target + " with the sword.";
        Location targetLocation = map.locationOf(target);
        Sword.Coating coating = sword.getCoating();
        if (coating == Sword.Coating.FIRE) {
            target.addStatus(new BurnEffect(target, 3, 5)); result += " The target is burning.";
        } else if (coating == Sword.Coating.CHARGED) {
            target.hurt(30); result += " The charge adds 30 damage.";
        } else if (coating == Sword.Coating.MELON) {
            Location centre = map.locationOf(target);
            for (Exit exit : centre.getExits()) {
                Location adjacent = exit.getDestination();
                if (adjacent.containsAnActor()) {
                    Actor splashTarget = adjacent.getActor();
                    splashTarget.hurt(15);
                    if (splashTarget instanceof HostileCreature) {
                        boolean splashKill = !splashTarget.isConscious();
                        ((HostileCreature) splashTarget).onAttacked(adjacent, splashKill);
                        if (splashKill) result += "\n" + splashTarget.unconscious(actor, map);
                    }
                }
            }
            result += " The melon coating explodes for 15 damage around the target.";
        }
        boolean killed = !target.isConscious();
        target.onAttacked(targetLocation, killed);
        if (killed) result += "\n" + target.unconscious(actor, map);
        return result;
    }
    @Override public String menuDescription(Actor actor) { return actor + " attacks " + target + " at " + direction + " with Sword"; }
}
