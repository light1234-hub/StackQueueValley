package game.quest;

import game.actor.CaveGhost;
import game.item.CopperItem;
import game.terrain.CopperNode;
import game.terrain.CorruptedDirt;
import game.terrain.Geode;
import game.terrain.Rock;

/**
 * Builds a hazardous relic chamber with ore, ghosts, and three ordered sigils.
 * The quest manager rejects sigils activated out of sequence.
 */
public class AncientRelicScenario implements QuestScenario {
    /**
     * Creates mining obstacles, corrupted tiles, ordered sigils, and a guardian.
     *
     * @param setup controlled arena setup operations
     */
    @Override
    public void prepare(QuestSetupContext setup) {
        setup.requireGround(7, 2, new Geode());
        setup.requireGround(8, 5, new CopperNode());
        setup.requireGround(8, 4, new Rock());
        for (int x = 9; x <= 11; x++) {
            setup.requireGround(x, 3, new CorruptedDirt());
        }
        setup.addObjective(5, 5, "Activate the first relic sigil");
        setup.addObjective(9, 2, "Activate the second relic sigil");
        setup.addObjective(12, 5, "Activate the final relic sigil");
        if (!setup.placeItem(11, 6, new CopperItem())) {
            throw new IllegalStateException("Could not place the relic offering");
        }
        if (!setup.spawnActor(12, 2, new CaveGhost())) {
            throw new IllegalStateException("Could not place relic guardian");
        }
    }
}

