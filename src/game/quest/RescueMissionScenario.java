package game.quest;

import game.actor.Bat;
import game.arena.ArenaQuest;
import game.item.ParsnipItem;
import game.terrain.Rock;
import game.terrain.Soil;

/**
 * Creates a rescue route split by impassable rocks and guarded by a bat.
 * The player must unlock two survivor cages before the rescue window closes.
 */
public class RescueMissionScenario implements QuestScenario {
    /** Builds rock obstacles, survivor cages, supplies, and a guarding enemy. */
    @Override
    public void prepare(ArenaQuest arena, QuestManager manager) {
        for (int y = 2; y <= 5; y++) {
            manager.requireGround(7, y, new Rock());
        }
        manager.requireGround(9, 5, new Soil());
        manager.addObjective(5, 2, "Unlock the first survivor cage");
        manager.addObjective(11, 5, "Unlock the second survivor cage");
        if (!arena.placeItem(9, 4, new ParsnipItem())) {
            throw new IllegalStateException("Could not place rescue supplies");
        }
        if (!arena.spawnActor(11, 2, new Bat())) {
            throw new IllegalStateException("Could not place rescue guardian");
        }
    }
}
