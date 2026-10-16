package game.cooking;

import edu.monash.fit2099.engine.items.Inventory;
import game.blueprint.Ingredient;
import game.ability.IngredientAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A class that represents a recipe where every ingredient from a certain
 * category is used
 */
public abstract class UseEverythingRecipe implements Recipe {
    private final IngredientAbility category;

    /**
     *Creates an object of the UseEverythingRecipe class
     * @param category
     */
    protected UseEverythingRecipe(IngredientAbility category) {
        if (category == null) {
            throw new RuntimeException("Ingredient category cannot be null.");
        }
        this.category = category;
    }

    /**
     * Returns the category whose ingredients this recipe uses.
     *
     * @return the ingredient category used by this recipe
     */
    protected final IngredientAbility ingredientCategory() {
        return category;
    }

    /**
     * Selects every unique ingredient in the csategory that is used by the recipe
     * @param inventory
     * @return all matching ingredients, empty if there are none
     */
    @Override
    public final Optional<List<Ingredient>> selectIngredients(Inventory inventory) {
        List<Ingredient> selected = new ArrayList<>();

        for (Ingredient ingredient : inventory.getItemsAs(Ingredient.class)) {
            if (ingredient.category() == category
                    && !isAlreadySelected(selected, ingredient)) {
                selected.add(ingredient);
            }
        }

        if (selected.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(List.copyOf(selected));
    }

    /**
     * Checks whether an item has already been selected
     * @param selected
     * @param candidate
     * @return true if the item selected is the same,false it is different
     */
    private boolean isAlreadySelected(List<Ingredient> selected, Ingredient candidate) {
        for (Ingredient chosen : selected) {
            if (chosen.getItem() == candidate.getItem()) {
                return true;
            }
        }
        return false;
    }
}