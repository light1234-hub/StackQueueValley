package game.effect;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.HostileCreature;
import game.blueprint.Coating;

/**
 * A weapon coating that damages actors adjacent to the attack target for 15.
 * The attacker's adjacency to the target means the blast can damage the
 * attacker too.
 */
public class MelonCoating implements Coating {
    @Override
    public String apply(Actor attacker, Actor target, GameMap map) {
        Location centre = map.locationOf(target);
        StringBuilder result = new StringBuilder(" The melon coating explodes for 15 damage around the target.");
        for (Exit exit : centre.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.containsAnActor()) continue;

            Actor splashTarget = adjacent.getActor();
            splashTarget.hurt(15);
            boolean killed = !splashTarget.isConscious();
            if (splashTarget instanceof HostileCreature) {
                ((HostileCreature) splashTarget).onAttacked(adjacent, killed);
            }
            if (killed) result.append(System.lineSeparator())
                    .append(splashTarget.unconscious(attacker, map));
        }
        return result.toString();
    }

    @Override
    public String getName() {
        return "Melon";
    }
}
