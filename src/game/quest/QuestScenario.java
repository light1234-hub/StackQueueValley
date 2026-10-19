package game.quest;

import game.arena.ArenaQuest;

/**
 * Defines how a contract changes the reusable arena and creates objectives.
 * Each implementation must set up at least one interactable objective.
 */
public interface QuestScenario {
    /**
     * Populates the arena with terrain, encounters and interactable goals.
     *
     * @param arena the cleared arena
     * @param manager the quest manager receiving objective registrations
     */
    void prepare(ArenaQuest arena, QuestManager manager);
}
