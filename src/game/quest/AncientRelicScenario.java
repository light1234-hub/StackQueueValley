package game.quest;

import game.actor.CaveGhost;
import game.arena.ArenaQuest;
import game.item.CopperItem;
import game.terrain.CopperNode;
import game.terrain.CorruptedDirt;
import game.terrain.Geode;
import game.terrain.Rock;

/**
 * Builds a hazardous relic chamber with ore, ghosts and three ordered sigils.
 * Activating the runes out of order is rejected by the quest manager.
 */
public final class AncientRelicScenario implements QuestScenario {
    /**
     * Creates mining obstacles, poison tiles, ordered sigils and an enemy.
     */
    @Override
    public void prepare(ArenaQuest arena, QuestManager manager) {
        manager.requireGround(7, 2, new Geode());
        manager.requireGround(8, 5, new CopperNode());
        manager.requireGround(8, 4, new Rock());
        for (int x = 9; x <= 11; x++) {
            manager.requireGround(x, 3, new CorruptedDirt());
        }
        manager.addObjective(5, 5, "Activate the first relic sigil");
        manager.addObjective(9, 2, "Activate the second relic sigil");
        manager.addObjective(12, 5, "Activate the final relic sigil");
        if (!arena.placeItem(11, 6, new CopperItem())) {
            throw new IllegalStateException("Could not place the relic offering");
        }
        if (!arena.spawnActor(12, 2, new CaveGhost())) {
            throw new IllegalStateException("Could not place relic guardian");
        }
    }
}

