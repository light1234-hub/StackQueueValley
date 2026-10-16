package game.cooking;

import edu.monash.fit2099.engine.items.Inventory;
import game.blueprint.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * A class that represents a recipe where a fixed number of ingredients is required
 */
public abstract class FixedAmountRecipe implements Recipe
{
    /**
     * The ingredient requirements for this recipe
     * @return the list of requirements that need to be satisfied for this recipe
     */
    protected abstract List<IngredientRequirement> requirements();

    /**
     * Attempts to select ingredients from the players inventory that
     * satisfy the requirements of the recipe
     * Ingredients can only be selected once
     * @param inventory
     * @return a read only list of the ingredients if the inventory can satsify the requirement
     * , returns a optional empty if the inventory cant fulfill the recipe
     */
    @Override
    public Optional<List<Ingredient>> selectIngredients(Inventory inventory)
    {
        List<Ingredient> available = inventory.getItemsAs(Ingredient.class);
        List<Ingredient> selected = new ArrayList<>();


        for (IngredientRequirement requirement : requirements())
        {
            int found = 0;

            for (Ingredient ingredient : available)
            {
                if (ingredient.category() == requirement.getCategory()
                && !isAlreadySelected(selected,ingredient))
                {
                    selected.add(ingredient);
                    found++;

                    if (found == requirement.getCount())
                    {
                        break;
                    }
                }
            }

            if (found < requirement.getCount())
            {
                return Optional.empty();
            }
        }

        return Optional.of(List.copyOf(selected));
    }

    /**
     * Checks whether an item has already been selected
     * @param selected
     * @param candidate
     * @return true if the item selected is the same,false it is different
     */
    private boolean isAlreadySelected(List<Ingredient> selected, Ingredient candidate)
    {
        for (Ingredient chosen : selected)
        {
            if (chosen.getItem() == candidate.getItem())
            {
                return true;
            }
        }

        return false;
    }
}
