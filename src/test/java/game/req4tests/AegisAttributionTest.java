package game.req4tests;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Bat;
import game.actor.CreatureControl;
import game.actor.Farmer;
import game.actor.Slime;
import game.item.AegisElixir;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import static game.req4tests.TestSupport.*;

import static org.junit.jupiter.api.Assertions.*;

/** Aegis must hit the creature that caused the damage, not an arbitrary neighbour. */
class AegisAttributionTest {

    @Test
    void reflectedDamageGoesToTheAttackerThatMatchesTheBlockedDamage() throws Exception {
        GameMap map = map();
        Location here = map.at(7, 4);
        Farmer farmer = farmerAt(map, 7, 4);
        Slime slime = new Slime();
        Bat bat = new Bat();
        map.at(8, 4).addActor(slime);
        map.at(6, 4).addActor(bat);
        int slimeDamage = CreatureControl.attackDamage(slime);
        int batDamage = CreatureControl.attackDamage(bat);
        Assumptions.assumeTrue(slimeDamage != batDamage, "needs two creatures with different damage");

        int slimeHp = slime.getStatistic(ActorStatistics.HEALTH);
        int batHp = bat.getStatistic(ActorStatistics.HEALTH);
        new AegisElixir().drink(farmer, here);

        farmer.hurt(batDamage);
        assertEquals(batHp - batDamage, bat.getStatistic(ActorStatistics.HEALTH));
        assertEquals(slimeHp, slime.getStatistic(ActorStatistics.HEALTH), "the slime did not attack");

        farmer.hurt(slimeDamage);
        assertEquals(slimeHp - slimeDamage, slime.getStatistic(ActorStatistics.HEALTH));
    }
}