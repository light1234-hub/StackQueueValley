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

    DiceChoice(String label, int sides, int bustAtOrBelow) {
        this.label = label;
        this.sides = sides;
        this.bustAtOrBelow = bustAtOrBelow;
    }

    /** @return number of sides on the selected die, or zero for forfeiture */
    public int sides() { return sides; }

    /** @return highest face that causes immediate failure */
    public int bustAtOrBelow() { return bustAtOrBelow; }

    /** @return a human-readable menu choice */
    public String menuText() {
        return switch (this) {
            case SAFE -> label + " (d6, no bust)";
            case RISKY -> label + " (d10, 1-2 bust)";
            case FORFEIT -> "Forfeit the dice challenge (-4 stamina)";
        };
    }
}
