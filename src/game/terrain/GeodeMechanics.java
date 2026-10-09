package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.effect.FireEffect;
import game.item.DiamondItem;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;


public final class GeodeMechanics {
    private static final Random random = new Random();

    private GeodeMechanics() {
    }

    /**
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

        // Collect the victims first so nobody is hurt twice: the breaker is
        // always hit, and is usually also standing on one of the adjacent tiles.
        Set<Actor> victims = new LinkedHashSet<>();
        victims.add(actor);
        for (Exit exit : explosionCentre.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                victims.add(adjacent.getActor());
            }
            adjacent.getGround().addStatus(new FireEffect(5, 0, 4, 10));
        }
        for (Actor victim : victims) {
            victim.hurt(25);
        }
        return actor + " breaks the geode and it explodes violently!";
    }
}