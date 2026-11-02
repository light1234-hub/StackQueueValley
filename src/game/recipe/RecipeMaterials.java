package game.recipe;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;

import java.util.*;

/**
 * SHared inventory selection and consumption
 * for cooking and crafting
 */
public final class RecipeMaterials {

    private RecipeMaterials(){

    }

    /**
     * Select distinct physical items satisfying all requirements
     * Does not modify the inventory
     * @param inventory inventory to search
     * @param requirements required materials
     * @return selected items, or empty if requirements cannot be met
     */
    public static Optional<List<Item>> select(Inventory inventory,List<ItemRequirement> requirements){
        List<Item> selected = new ArrayList<>();
        Set<Item> used = Collections.newSetFromMap(new IdentityHashMap<>());

        for(ItemRequirement requirement : requirements){
            int remaining = requirement.getCount();

            for (Item item : inventory.getItems()){
                if(remaining > 0 && !used.contains(item) && requirement.matches(item)){
                    selected.add(item);
                    used.add(item);
                    remaining--;
                }
            }
            if(remaining > 0){
                return Optional.empty();
            }
        }
        return Optional.of(List.copyOf(selected));
    }

    /**
     * Consumes the selected items after checking that they are still in the inventory
     * @param inventory inventory containing items
     * @param selected selected items to consume
     * @return true if the items were consumed
     */
    public static boolean consume(Inventory inventory, List<Item>selected){
        if(!inventory.getItems().containsAll(selected)){
            return false;
        }
        for(Item item: selected){
            inventory.remove(item);
        }
        return true;
    }
}
