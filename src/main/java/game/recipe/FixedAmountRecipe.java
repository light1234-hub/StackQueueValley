package game.recipe;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import game.blueprint.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Represents cooking recipes requiring fixed quantities
 * of ingredients.
 * Uses the shared RecipeMaterials selection logic.
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
        List<ItemRequirement> rules = new ArrayList<>();


        for (IngredientRequirement required : requirements())
        {
            rules.add(new ItemRequirement
                    (item -> item.asCapability(Ingredient.class)
                            .map(ingredient -> ingredient.category() == required.getCategory())
                            .orElse(false),
                            required.getCount(),
                            required.getCategory().name()));
        }

        return RecipeMaterials.select(inventory,rules).map(items -> {
            List<Ingredient> ingredients = new ArrayList<>();
            for (Item item : items){
                ingredients.add(item.asCapability(Ingredient.class).orElseThrow());
            }
            return List.copyOf(ingredients);
        });
    }


}
