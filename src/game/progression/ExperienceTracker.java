package game.progression;

/**
 * Manages the Farmer's experience points (XP) and level progression.
 * Tracks total earned XP and available XP separately, ensuring that
 * spending XP does not affect the Farmer's level or unlocked abilities.
 */
public final class ExperienceTracker {
    private int totalEarned;
    private int available;

    /**
     * Awards experience points to the Farmer.
     * Updates both total earned XP and available XP.
     *
     * @param amount the amount of XP to award
     * @throws IllegalArgumentException if the amount is negative
     * @throws ArithmeticException if the XP values overflow
     */
    public void award(int amount){
        if(amount < 0){
            throw new IllegalArgumentException("XP award cannot be negative!");
        }

        int newTotal = Math.addExact(totalEarned,amount);
        int newAvailable = Math.addExact(available,amount);

        totalEarned = newTotal;
        available = newAvailable;
    }

    /**
     * Spends experience points from the Farmer's available XP.
     * Total earned XP remains unchanged.
     *
     * @param amount the amount of XP to spend
     * @return true if the XP was successfully spent, false if insufficient
     * @throws IllegalArgumentException if the amount is zero or negative
     */
    public boolean spend(int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("XP award cannot be negative!");
        }

        if (available < amount){
            return false;
        }

        available -= amount;
        return true;
    }
    /**
     * Returns the total experience points earned by the Farmer.
     *
     * @return the total earned XP
     */
    public int totalEarned(){
        return totalEarned;
    }

    /**
     * Returns the Farmer's remaining spendable experience points.
     *
     * @return the available XP
     */
    public int available(){
        return available;
    }

    /**
     * Calculates the Farmer's level based on total earned XP.
     * The Farmer starts at level 1, with subsequent level thresholds
     * increasing quadratically.
     *
     * @return the Farmer's current level
     */
    public int level(){
        int curr = 1;
        while(100L * curr *curr <= totalEarned){
            curr++;
        }
        return curr;
    }

}
