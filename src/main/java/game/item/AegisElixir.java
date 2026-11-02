package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.effect.ReflectStatus;

import java.util.LinkedHashMap;
import java.util.Map;

/** Aegis Elixir: for 10 turns the drinker takes no damage and attackers take the blocked damage. */
public class AegisElixir extends Potion {
    private static final int DURATION = 10;

    public AegisElixir() {
        super("Aegis Elixir", '!', recipe(), 100, 4);
    }

    private static Map<Class<? extends Item>, Integer> recipe() {
        Map<Class<? extends Item>, Integer> recipe = new LinkedHashMap<>();
        recipe.put(StoneItem.class, 3);
        recipe.put(StrawberryItem.class, 2);
        recipe.put(DiamondItem.class, 1);
        return recipe;
    }

    @Override
    public String drink(Actor drinker, Location location) {
        ReflectStatus.applyTo(drinker, location, DURATION);
        return drinker + " drinks the Aegis Elixir and is shielded by a reflective ward for "
                + DURATION + " turns.";
    }
}