package game.quest;

import game.terrain.LivingGroveGround;
import game.terrain.SpreadingBlightGround;

/**
 * Applies the long-term ecological consequences of the Cursed Forest contract.
 * Success establishes a restorative grove; failure introduces spreading blight.
 */
public class ForestConsequence implements QuestConsequence {

    /**
     * Creates a Living Grove that purifies corrupted terrain and supports plant growth.
     *
     * @param world context for modifying the persistent farm
     * @return a description of the resulting world change
     */
    @Override
    public String onSuccess(QuestWorldContext world) {
        boolean placed = world.placeGroundNearby(
                0, 3, LivingGroveGround::new) != null;
        return placed
                ? "A Living Grove takes root. It gradually purifies nearby blight and grows vegetation."
                : "The grove could not take root because the nearby farm plots were unavailable.";
    }

    /**
     * Creates up to two blight sources that spread and poison actors over time.
     *
     * @param world context for modifying the persistent farm
     * @return a description of the resulting world change
     */
    @Override
    public String onFailure(QuestWorldContext world) {
        boolean first = world.placeGroundNearby(
                0, 3, SpreadingBlightGround::new) != null;
        boolean second = world.placeGroundNearby(
                1, 5, SpreadingBlightGround::new) != null;
        int placed = (first ? 1 : 0) + (second ? 1 : 0);
        return placed > 0
                ? "Spreading Blight has reached the farm in " + placed
                + " location(s). It infects nearby terrain and poisons actors standing on it."
                : "The blight could not take hold because the nearby farm plots were unavailable.";
    }
}
