package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.PortableResourceTransfer;
import game.terrain.Dirt;
import game.terrain.TileFinder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prospector's Elixir: every portable item on the ground within radius 4 is pulled
 * into the drinker's inventory, then every Rock in range is shattered into Dirt and
 * Stone items left on the ground.
 */
public class ProspectorElixir extends Potion {
    private static final int RADIUS = 4;
    private static final int STONES_PER_ROCK = 1;

    public ProspectorElixir() {
        super("Prospector's Elixir", '!', recipe(), 150, 5);
    }

    private static Map<Class<? extends Item>, Integer> recipe() {
        Map<Class<? extends Item>, Integer> recipe = new LinkedHashMap<>();
        recipe.put(CopperItem.class, 2);
        recipe.put(TomatoItem.class, 2);
        recipe.put(StoneItem.class, 1);
        return recipe;
    }

    @Override
    public String drink(Actor drinker, Location location) {
        List<Location> area = new ArrayList<>(location.getNearbyLocations(RADIUS));
        area.add(location);

        int pulled = 0;
        for (Location tile : area) {
            for (Item item : tile.getItems()) {
                if (item.hasAbility(ItemAbility.PORTABLE)) {
                    pulled++;
                }
            }
            PortableResourceTransfer.collect(drinker, tile);
        }

        int shattered = 0;
        for (Location tile : area) {
            if (TileFinder.isRock(tile)) {
                tile.setGround(new Dirt());
                for (int i = 0; i < STONES_PER_ROCK; i++) {
                    tile.addItem(new StoneItem());
                }
                shattered++;
            }
        }
        return drinker + " drinks the Prospector's Elixir: " + pulled + " item(s) pulled in, "
                + shattered + " rock(s) shattered.";
    }
}