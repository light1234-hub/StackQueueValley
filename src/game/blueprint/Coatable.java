package game.blueprint;

/**
 * Marks an item or object that can hold a weapon coating.
 *
 * <p>Implementations own their coating state, allowing actions to coat future
 * weapon or tool types without depending directly on {@code Sword}.</p>
 */
public interface Coatable {
    /** Returns the current coating, or {@code null} when none is applied. */
    Coating getCoating();

    /** Replaces the current coating. Passing {@code null} clears it. */
    void setCoating(Coating coating);
}
