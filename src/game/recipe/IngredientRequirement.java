package game.recipe;

import game.ability.IngredientAbility;

/**
 * Represents the number of ingredients required from a single category
 */
public final class IngredientRequirement
{
    private final IngredientAbility category;
    private final int count;

    /**
     * A creates an object of the ingredient requirement class if the
     * category and ingredient count is reasonable
     * @param category
     * @param count
     */
    public IngredientRequirement(IngredientAbility category, int count)
    {
        if (category == null)
        {
            throw new RuntimeException("Ingredient category cannot be null");
        }

        if (count < 1)
        {
            throw new RuntimeException("Ingredient count must be a positive number");
        }

        this.category = category;
        this.count = count;
    }

    /**
     * A getter that returns the category of the requirement
     * @return the category
     */
    public IngredientAbility getCategory()
    {
        return category;
    }

    /**
     * A getter that returns the ingredient count of the requirement
     * @return the count
     */
    public int getCount()
    {
        return count;
    }
}
