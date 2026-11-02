package game.actor;

import game.behaviour.SurvivorResourceBehaviour;

/**
 * A non-player character with a resource-gathering and farmland-repair brain.
 * The actor holds no quest-specific pathfinding or turn-decision logic.
 */
public class RescuedSurvivor extends NPC {
    /** Creates a survivor assigned to the given supply camp coordinates. */
    public RescuedSurvivor(int campX, int campY) {
        super("Rescued Survivor", 'v', 40);
        addBehaviour(new SurvivorResourceBehaviour(campX, campY));
    }
}

