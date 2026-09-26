package game.terrain;

import game.blueprint.Processable;
import game.item.DriedProduct;

/**
 * Represents the DryingRack location where items can be Dried
 */
public class DryingRack extends ProcessingFacility
{
    /**
     * A constructor that creates an object of the DryingRack class
     */
    public DryingRack()
    {
        super("Drying Rack", 'R');
    }

    /**
     * A method that handles the modifying of the item's name after Drying
     * @param preProcessedProduct
     * @return an item with a modified name to reflect the Drying it underwent
     */
    public DriedProduct process(Processable preProcessedProduct)
    {
        return new DriedProduct(preProcessedProduct);
    }


}
