package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import game.blueprint.Processable;

/**
 * Represents an item after it has been processed by a Processing Facility
 */
public abstract class ProcessedProduct extends ConsumableItem implements Processable
{
    private final Processable preProcessedProduct;

    /**
     * A constructor that creates an object of the ProcessedProduct class
     * @param preProcessedProduct
     * @param nameModifier
     */
    protected ProcessedProduct(Processable preProcessedProduct, String nameModifier)
    {
        super(nameModifier + " " + preProcessedProduct, preProcessedProduct.getDisplayChar());
        this.preProcessedProduct = preProcessedProduct;
    }

    /**
     * An abstract method that applies the effects of the processing
     * @param actor
     */
    protected abstract void applyOwnEffect(Actor actor);

    /**
     * An abstract method that create the effect description of the Processing
     * @param actor
     * @return A description of the processed product
     */
    protected abstract String ownEffectDescrption(Actor actor);

    /**
     * A method that applies the item's own effect and the effects
     * of the processing on top of that
     * @param actor
     */
    @Override
    public void applyEffect(Actor actor)
    {
        preProcessedProduct.applyEffect(actor);
        applyOwnEffect(actor);
    }

    /**
     * A method that outputs the results of the consumption of an item
     * Also includes the effects of any processing that happened to the item
     * @param actor
     * @return Describes the consumption of an item and it's processing
     */
    @Override
    public String consume(Actor actor)
    {
        return preProcessedProduct.consume(actor) + " "
                + ownEffectDescrption(actor);
    }
}
