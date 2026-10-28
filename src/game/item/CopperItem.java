package game.item;

import edu.monash.fit2099.engine.items.Item;
import game.recipe.CraftingMaterial;
import game.recipe.CraftingMaterialType;

/**
 * A piece of copper, dropped whenever a copper node is broken.
 * can be used as crafting material
 */
public class CopperItem extends Item implements CraftingMaterial {
    public CopperItem() {
        super("Copper", 'c');
        this.makePortable();
    }

    @Override
    public CraftingMaterialType materialType(){
        return CraftingMaterialType.COPPER;
    }
}
