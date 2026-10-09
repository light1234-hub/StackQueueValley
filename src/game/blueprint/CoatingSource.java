package game.blueprint;

/**
 * Provides a coating that can be applied to a {@link Coatable} item.
 *
 * <p>Items, terrain, or active ground effects can implement this interface.
 * The farmer discovers sources through this capability rather than checking
 * specific source classes.
 */
public interface CoatingSource {
    /** Creates the coating provided by this source. */
    Coating createCoating();

    /**
     * Indicates whether an item source is consumed after use. Ground effects
     * and reusable sources should keep the default value.
     */
    default boolean isConsumedOnUse() {
        return false;
    }
}
