package game.quest;

/**
 * Defines how a contract prepares the reusable quest arena.
 * Implementations specify their terrain, encounters, resources, and objectives.
 */
public interface QuestScenario {
    /**
     * Populates the arena with terrain, encounters, and interactable objectives.
     *
     * @param setup controlled operations for preparing the active arena
     */
    void prepare(QuestSetupContext setup);
}
