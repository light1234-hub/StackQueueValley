package game.quest;

import game.actor.Slime;
import game.arena.ArenaQuest;
import game.terrain.CorruptedDirt;
import game.terrain.Soil;

/**
 * Creates a corrupted woodland with three ritual roots and a roaming slime.
 * The player must purify each root before the forest infects the farm.
 */
public class CursedForestScenario implements QuestScenario {
    /** Dynamically configures terrain, an enemy, and three objectives. */
    @Override
    public void prepare(ArenaQuest arena, QuestManager manager) {
        for (int x = 5; x <= 9; x++) {
            manager.requireGround(x, 3, new CorruptedDirt());
        }
        manager.requireGround(10, 3, new Soil());
        manager.addObjective(7, 2, "Purify the first cursed root");
        manager.addObjective(10, 4, "Purify the second cursed root");
        manager.addObjective(11, 6, "Purify the final cursed root");
        if (!arena.spawnActor(12, 5, new Slime())) {
            throw new IllegalStateException("Could not place forest guardian");
        }
    }
}
