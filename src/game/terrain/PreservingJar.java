package game.terrain;

import game.blueprint.Processable;
import game.item.PreservedProduct;

/**
 * Represents the PreservingJar locations where items can be preserved
 */
public class PreservingJar extends ProcessingFacility
{
    /**
     * A constructor that creates an object of the Preserving Jar class
     */
    public PreservingJar()
    {
        super("Preserving Jar",'J');
    }

    /**
     * A method that handles the modifying of the item's name after Preserving
     * @param preProcessedProduct
     * @return an item with a modified name to reflect the Preserving it underwent
     */
    public PreservedProduct process(Processable preProcessedProduct)
    {
        return new PreservedProduct(preProcessedProduct);
    }
}
