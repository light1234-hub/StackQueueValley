package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/** Impassable stone boundary of the reusable quest arena. */
public class QuestArenaWall extends Ground {
    /** Creates a wall shown as '#'. */
    public QuestArenaWall() {
        super('#', "Quest Arena wall");
    }

    /** {@inheritDoc} */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}
