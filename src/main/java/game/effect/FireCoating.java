package game.effect;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.blueprint.Coating;

/** A weapon coating that burns its target for 5 damage per turn for 3 turns. */
public class FireCoating implements Coating {
    @Override
    public String apply(Actor attacker, Actor target, GameMap map) {
        target.addStatus(new BurnEffect(target, 3, 5));
        return " The target is burning.";
    }

    @Override
    public String getName() {
        return "Fire";
    }
}
