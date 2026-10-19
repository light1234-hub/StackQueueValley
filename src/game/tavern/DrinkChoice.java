package game.tavern;

/** Drinking strategies available in each round of the Tavern wager. */
public enum DrinkChoice {
    /** Drink one cup without risking a blackout. */
    SIP(1, 0.0),
    /** Drink two cups, with a twenty-percent chance of blacking out. */
    CHUG(2, 0.20);

    private final int cups;
    private final double blackoutChance;

    /** @param cups cups scored this round
     * @param blackoutChance probability of immediate contest loss
     */
    DrinkChoice(int cups, double blackoutChance) {
        this.cups = cups;
        this.blackoutChance = blackoutChance;
    }

    /** @return cups gained by choosing this strategy */
    public int cups() { return cups; }

    /** @return probability of a blackout, between zero and one */
    public double blackoutChance() { return blackoutChance; }
}
