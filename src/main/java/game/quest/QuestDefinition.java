package game.quest;

/**
 * Immutable contract configuration; both effects depend on abstractions.
 *
 * @param title the name shown on the quest board
 * @param entryFee coins paid when accepting the contract
 * @param coinReward coins earned only on success
 * @param experienceReward experience earned only on success
 * @param turnLimit maximum number of arena turns
 * @param orderedObjectives whether the objectives must be activated in order
 * @param scenario world setup for the contract
 * @param consequence persistent success/failure effects
 */
public record QuestDefinition(String title, int entryFee, int coinReward, int experienceReward, int turnLimit, boolean orderedObjectives, QuestScenario scenario, QuestConsequence consequence) {
    /** Validates the contract before it becomes available. */
    public QuestDefinition {
        if (title == null || title.isBlank() || scenario == null || consequence == null || entryFee < 0 || coinReward < 0 || experienceReward < 0 || turnLimit < 1) {
            throw new IllegalArgumentException("Invalid quest definition");
        }
    }
}
