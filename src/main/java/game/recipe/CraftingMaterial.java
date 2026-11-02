package game.recipe;

/**
 * Represent item that can be used as a crafting material
 */
public interface CraftingMaterial {
    /**
     * returns the material category of this item
     * @return the crafting material type
     */
    CraftingMaterialType materialType();
}
