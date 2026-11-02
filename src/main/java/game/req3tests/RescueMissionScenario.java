package game.req3tests;

import game.actor.Bat;
import game.item.ParsnipItem;
import game.terrain.Rock;
import game.terrain.Soil;

/**
 * Creates a rescue route split by impassable rocks and guarded by a bat.
 * The player must unlock two survivor cages before the rescue window closes.
 */
public class RescueMissionScenario implements QuestScenario {
    /**
     * Builds rock obstacles, survivor cages, supplies, and a guarding enemy.
     *
     * @param setup controlled arena setup operations
     */
    @Override
    public void prepare(QuestSetupContext setup) {
        for (int y = 2; y <= 5; y++) {
            setup.requireGround(7, y, new Rock());
        }
        setup.requireGround(9, 5, new Soil());
        setup.addObjective(5, 2, "Unlock the first survivor cage");
        setup.addObjective(11, 5, "Unlock the second survivor cage");
        setup.requireItem(9, 4, new ParsnipItem());
        setup.requireActor(11, 2, new Bat());
    }
}
