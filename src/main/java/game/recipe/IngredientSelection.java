package game.recipe;

import game.blueprint.Ingredient;

import java.util.List;

/**
 * A helper class for recipe selection
 */
final class IngredientSelection
{
    private IngredientSelection()
    {}

    /**
     * Checks whether an item has already been selected
     * @param selected
     * @param candidate
     * @return true if the item selected is the same,false it is different
     */
    static boolean isAlreadySelected(List<Ingredient> selected, Ingredient candidate)
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
