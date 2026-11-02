package game.req3tests;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/** Hidden arena tile that advances the quest deadline once per world turn. */
public final class QuestClockGround extends Ground {
    private final QuestManager manager;

    /**
     * Performs QuestClockGround for this QuestClockGround.
     *
     * @param manager quest manager whose deadline this clock advances
     */
    public QuestClockGround(QuestManager manager) {
        super('.', "Quest clock");
        this.manager = manager;
    }

    /**
     * Advances the active quest only while the player is inside the arena.
     *
     * @param location tile containing the clock
     */
    @Override
    public void tick(Location location) {
        manager.tick();
    }
}
