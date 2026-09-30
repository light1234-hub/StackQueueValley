package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.effect.FireEffect;
import game.item.DiamondItem;

import java.util.Random;

/**
 * Shared logic for resolving a broken geode, whether it was lying on the
 * ground (game.terrain.Geode) or being carried
 * (game.farmeraction.BreakGeodeFromInventoryAction). Kept in one place so
 * both paths stay consistent instead of duplicating the 50/50 roll twice.
 */
public final class GeodeMechanics {
    private static final Random random = new Random();

    private GeodeMechanics() {
    }

    /**
     * Resolves the outcome of breaking a geode: 50% chance of a diamond,
     * otherwise it explodes, damaging the breaker and every adjacent actor
     * for 25, and setting adjacent locations on fire for 5 turns (10 damage
     * per turn), which then burns anyone who walks through for 4 further
     * turns at 10 damage per turn.
     *
     * @param actor the actor breaking the geode
     * @param explosionCentre the location the diamond/explosion is centred
     *                        on -- the geode's own location if broken from
     *                        the ground, or the farmer's current location if
     *                        broken from inventory
     * @return a description of what happened
     */
    public static String resolve(Actor actor, Location explosionCentre) {
        if (random.nextBoolean()) {
            actor.getInventory().add(new DiamondItem());
            return actor + " breaks the geode and finds a diamond!";
        }

        actor.hurt(25);
        for (Exit exit : explosionCentre.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                adjacent.getActor().hurt(25);
            }
            adjacent.getGround().addStatus(new FireEffect(5, 10, 4, 10));
        }
        return actor + " breaks the geode and it explodes violently!";
    }
}
