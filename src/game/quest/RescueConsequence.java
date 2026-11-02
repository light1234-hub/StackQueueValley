package game.quest;

import edu.monash.fit2099.engine.positions.Location;
import game.actor.RescuedSurvivor;
import game.actor.Scavenger;
import game.terrain.SurvivorCampGround;

/**
 * Applies the lasting outcomes of the Lost Survivors contract. Success creates an
 * autonomous survivor settlement; failure allows hostile scavengers to occupy the farm.
 */
public class RescueConsequence implements QuestConsequence {

    /**
     * Establishes supply camps and attempts to spawn autonomous survivors that gather
     * resources and repair eligible farm terrain.
     *
     * @param world context for modifying the persistent farm
     * @return a description of the resulting world change
     */
    @Override
    public String onSuccess(QuestWorldContext world) {
        Location firstCamp = world.placeGroundNearby(2, 4, SurvivorCampGround::new);
        Location secondCamp = world.placeGroundNearby(2, 5, SurvivorCampGround::new);
        boolean firstSurvivor = firstCamp != null && world.spawnNearby(7, 5, new RescuedSurvivor(firstCamp.x(), firstCamp.y()));
        boolean secondSurvivor = secondCamp != null && world.spawnNearby(7, 4, new RescuedSurvivor(secondCamp.x(), secondCamp.y()));
        int camps = (firstCamp == null ? 0 : 1) + (secondCamp == null ? 0 : 1);
        int survivors = (firstSurvivor ? 1 : 0) + (secondSurvivor ? 1 : 0);
        return "Established " + camps + " survivor camp and spawned " + survivors + " survivor. Survivors gather portable resources and repair damaged ground over time.";
    }

    /**
     * Spawns scavengers that steal portable items and build temporary movement-blocking traps.
     *
     * @param world context for modifying the persistent farm
     * @return a description of the resulting world change
     */
    @Override
    public String onFailure(QuestWorldContext world) {
        boolean first = world.spawnNearby(7, 5, new Scavenger());
        boolean second = world.spawnNearby(7, 4, new Scavenger());
        int spawned = (first ? 1 : 0) + (second ? 1 : 0);
        return spawned > 0
                ? spawned + " scavenger have occupied the farm. They steal resources and build temporary snares." : "No scavengers could enter because the available spawning locations were blocked.";
    }
}
