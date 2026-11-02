package game.questboard;

/** Strategies available during each three-round quest-board dice challenge. */
public enum DiceChoice {
    /** Roll 1-6; every result counts toward the target. */
    SAFE("Safe roll", 6, 0),
    /** Roll 1-10; rolling 1 or 2 immediately loses the challenge. */
    RISKY("Risky roll", 10, 2),
    /** End the challenge early and take the usual loss penalty. */
    FORFEIT("Forfeit", 0, 0);

    private final String label;
    private final int sides;
    private final int bustAtOrBelow;

    /**
     * Creates a dice choice with its display label, number of sides and bust threshold.
     *
     * @param label text displayed for the dice choice
     * @param sides number of sides on the die
     * @param bustAtOrBelow highest roll that causes an immediate loss
     */
    DiceChoice(String label, int sides, int bustAtOrBelow) {
        this.label = label;
        this.sides = sides;
        this.bustAtOrBelow = bustAtOrBelow;
    }

    /**
     * Returns the number of sides on the chosen die.
     *
     * @return number of sides, or zero for forfeiting
     */
    public int sides() { return sides; }

    /**
     * Returns the highest roll that triggers an immediate loss.
     *
     * @return largest die value that causes an immediate loss
     */
    public int bustAtOrBelow() { return bustAtOrBelow; }

    /**
     * Returns the menu description of this dice choice.
     *
     * @return description displayed for the selected dice choice
     */
    public String menuText() {
        return switch (this) {
            case SAFE -> label + " (d6, no bust)";
            case RISKY -> label + " (d10, 1-2 bust)";
            case FORFEIT -> "Forfeit the dice challenge (-4 stamina)";
        };
    }
}
