package game.item;

import edu.monash.fit2099.engine.actors.Actor;
import game.blueprint.Processable;

/**
 * Represents an Item that has been Preserved using a PreservingJar
 */
public class PreservedProduct extends ProcessedProduct
{
    private static final String NAME_MODIFIER = "Preserved";
    private static final int BONUS_HEALTH = 1;

    /**
     * A constructor that creates an object of the PreservedProduct class
     * @param preProcessedProduct
     */
    public PreservedProduct(Processable preProcessedProduct)
    {
        super(preProcessedProduct,NAME_MODIFIER);
    }

    /**
     * An abstract method that applies the effects of the preserving
     * @param actor
     */
    @Override
    protected void applyOwnEffect(Actor actor)
    {
        actor.heal(BONUS_HEALTH);
    }

    /**
     * An abstract method that create the effect description of the preserving
     * @param actor
     * @return a description of the preserved product
     */
    @Override
    protected String ownEffectDescrption(Actor actor)
    {
        return "The Preserved effect grants the Farmer an extra " +
                BONUS_HEALTH + " Health";
    }
}

