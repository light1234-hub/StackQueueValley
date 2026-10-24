package game.tavern;

/** Drinking strategies available in each round of the Tavern wager. */
public enum DrinkChoice {
    /** Drink one cup without risking a blackout. */
    SIP(1, 0.0, "Sip"),
    /** Drink two cups, with a twenty-percent chance of blacking out. */
    CHUG(2, 0.20, "Chug");

    private final int cups;
    private final double blackoutChance;
    private final String label;

    /** @param cups cups scored this round
     * @param blackoutChance probability of immediate contest loss
     * @param label player-facing name of this drinking strategy
     */
    DrinkChoice(int cups, double blackoutChance, String label) {
        this.cups = cups;
        this.blackoutChance = blackoutChance;
        this.label = label;
    }

    /** @return cups gained by choosing this strategy */
    public int cups() { return cups; }

    /** @return probability of a blackout, between zero and one */
    public double blackoutChance() { return blackoutChance; }

    /** @return menu text derived from the same values the contest uses */
    public String menuText() {
        String risk = blackoutChance == 0.0 ? "safe"
                : (int) Math.round(blackoutChance * 100) + "% blackout risk";
        return "Drink: " + label + " (+" + cups + " "
                + (cups == 1 ? "cup" : "cups") + ", " + risk + ")";
    }
}
