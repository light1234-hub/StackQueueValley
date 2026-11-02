package game.req3tests;

import game.actor.Slime;
import game.terrain.CorruptedDirt;
import game.terrain.Soil;

/**
 * Creates a corrupted woodland with three ritual roots and a roaming slime.
 * The player must purify each root before the forest infects the farm.
 */
public class CursedForestScenario implements QuestScenario {
    /**
     * Creates corrupted terrain, a forest guardian, and three objectives.
     *
     * @param setup controlled arena setup operations
     */
    @Override
    public void prepare(QuestSetupContext setup) {
        for (int x = 5; x <= 9; x++) {
            setup.requireGround(x, 3, new CorruptedDirt());
        }
        setup.requireGround(10, 3, new Soil());
        setup.addObjective(7, 2, "Purify the first cursed root");
        setup.addObjective(10, 4, "Purify the second cursed root");
        setup.addObjective(11, 6, "Purify the final cursed root");
        setup.requireActor(12, 5, new Slime());
    }
}
