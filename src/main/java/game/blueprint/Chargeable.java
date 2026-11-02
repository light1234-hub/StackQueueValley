package game.blueprint;

/**
 * Represents ground that is capable of holding an electrical charge.
 * In the current version of the game, only Puddle implements this.
 */
public interface Chargeable {
    /**
     * Charges this ground, causing it to damage any actor standing on it
     * every turn for as long as the charge remains.
     */
    void charge();
}