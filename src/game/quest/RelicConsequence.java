package game.quest;

import edu.monash.fit2099.engine.positions.Location;
import game.terrain.ResonantCrystalGround;
import game.terrain.UnstableGround;

/**
 * Applies the lasting outcomes of the Ancient Relic contract. Success creates a
 * connected resonant crystal vein; failure destabilizes a region of the farm.
 */
public class RelicConsequence implements QuestConsequence {

    /**
     * Creates a connected crystal vein that releases resources and may expose Copper Nodes
     * when mined.
     *
     * @param world context for modifying the persistent farm
     * @return a description of the resulting world change
     */
    @Override
    public String onSuccess(QuestWorldContext world) {
        Location first = world.placeGroundNearby(
                3, 4, ResonantCrystalGround::new);
        Location second = first == null ? null
                : world.placeAdjacentGround(first, ResonantCrystalGround::new);
        Location third = second == null ? null
                : world.placeAdjacentGround(second, ResonantCrystalGround::new);

        int crystals = (first == null ? 0 : 1)
                + (second == null ? 0 : 1)
                + (third == null ? 0 : 1);
        return crystals > 0
                ? "A resonant vein of " + crystals
                + " crystal(s) appears. Mining one breaks connected crystals, drops resources and may expose a Copper Node."
                : "The crystal vein could not form because the nearby farm plots were unavailable.";
    }

    /**
     * Creates up to three unstable ground tiles whose timed collapses displace actors,
     * scatter loose items and leave blocking rubble.
     *
     * @param world context for modifying the persistent farm
     * @return a description of the resulting world change
     */
    @Override
    public String onFailure(QuestWorldContext world) {
        boolean first = world.placeGroundNearby(8, 0, UnstableGround::new) != null;
        boolean second = world.placeGroundNearby(8, 1, UnstableGround::new) != null;
        boolean third = world.placeGroundNearby(9, 0, UnstableGround::new) != null;
        int unstableTiles = (first ? 1 : 0) + (second ? 1 : 0) + (third ? 1 : 0);
        return unstableTiles > 0
                ? unstableTiles + " unstable ground tile(s) appeared. They collapse after three turns, "
                + "displacing nearby actors, scattering items and leaving rubble."
                : "The collapse could not begin because the nearby farm plots were unavailable.";
    }
}
