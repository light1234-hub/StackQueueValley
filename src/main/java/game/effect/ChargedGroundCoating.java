package game.effect;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Coating;

/** A weapon coating that deals 30 additional damage on every attack. */
public class ChargedGroundCoating implements Coating {
    @Override
    public String apply(Actor attacker, Actor target, GameMap map) {
        target.hurt(30);
        return " The charge adds 30 damage.";
    }

    @Override
    public String getName() {
        return "charged ground";
    }
}
