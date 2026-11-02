package game.tests;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.ControlledZombie;
import game.actor.CreatureControl;
import game.actor.Farmer;
import game.actor.Slime;
import game.actor.Zombie;
import game.item.AegisElixir;
import game.item.CopperItem;
import game.item.GaleDraught;
import game.item.ProspectorElixir;
import game.item.StoneItem;
import game.terrain.Dirt;
import game.terrain.Rock;
import org.junit.jupiter.api.Test;

import static game.tests.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

/** Drinking each potion. The potion's drink method is called directly with the drinker's location. */
class PotionEffectsTest {

    @Test
    void galePushesActorsThreeTilesAwayAndStunsHostiles() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 7, 4);
        Slime slime = new Slime();
        map.at(8, 4).addActor(slime);
        new GaleDraught().drink(farmer, map.at(7, 4));
        assertSame(slime, map.at(11, 4).getActor());
        assertTrue(CreatureControl.isStunned(slime));
        assertSame(farmer, map.at(7, 4).getActor(), "the drinker does not move");
    }

    @Test
    void galePushStopsEarlyAtObstacle() throws Exception {
        GameMap map = map(new int[]{10, 4});
        Farmer farmer = farmerAt(map, 7, 4);
        Slime slime = new Slime();
        map.at(8, 4).addActor(slime);
        new GaleDraught().drink(farmer, map.at(7, 4));
        assertSame(slime, map.at(9, 4).getActor());
    }

    @Test
    void galeIgnoresActorsOutsideRadiusThree() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 2, 4);
        Slime far = new Slime();
        map.at(7, 4).addActor(far);
        new GaleDraught().drink(farmer, map.at(2, 4));
        assertSame(far, map.at(7, 4).getActor());
        assertFalse(CreatureControl.isStunned(far));
    }

    @Test
    void galeStopsAtMapEdge() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 12, 4);
        Slime slime = new Slime();
        map.at(13, 4).addActor(slime);
        new GaleDraught().drink(farmer, map.at(12, 4));
        assertSame(slime, map.at(14, 4).getActor());
    }

    @Test
    void galeStunsAZombieByReplacingItWithAControlledZombie() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 7, 4);
        map.at(8, 4).addActor(new Zombie());
        new GaleDraught().drink(farmer, map.at(7, 4));
        var pushed = map.at(11, 4).getActor();
        assertEquals(ControlledZombie.class, pushed.getClass());
        assertTrue(CreatureControl.isStunned(pushed));
    }

    @Test
    void stunWearsOffAfterTwoTurns() throws Exception {
        GameMap map = map();
        Slime slime = new Slime();
        Location l = map.at(5, 5);
        l.addActor(slime);
        CreatureControl.stun(slime, map, 2);
        slime.tickStatuses(l);
        assertTrue(CreatureControl.isStunned(slime));
        slime.tickStatuses(l);
        assertFalse(CreatureControl.isStunned(slime));
    }

    @Test
    void aegisBlocksDamageAndReflectsItForTenTurns() throws Exception {
        GameMap map = map();
        Location here = map.at(7, 4);
        Farmer farmer = farmerAt(map, 7, 4);
        Slime slime = new Slime();
        map.at(8, 4).addActor(slime);
        int farmerHp = farmer.getStatistic(ActorStatistics.HEALTH);
        int slimeHp = slime.getStatistic(ActorStatistics.HEALTH);

        new AegisElixir().drink(farmer, here);
        farmer.hurt(10);
        assertEquals(farmerHp, farmer.getStatistic(ActorStatistics.HEALTH), "no damage taken");
        assertEquals(slimeHp - 10, slime.getStatistic(ActorStatistics.HEALTH), "attacker takes blocked damage");

        for (int i = 0; i < 9; i++) {
            farmer.tickStatuses(here);
        }
        farmer.hurt(5);
        assertEquals(farmerHp, farmer.getStatistic(ActorStatistics.HEALTH), "still active on the 10th turn");

        farmer.tickStatuses(here);
        farmer.hurt(5);
        assertEquals(farmerHp - 5, farmer.getStatistic(ActorStatistics.HEALTH), "damage applies after expiry");
    }

    @Test
    void prospectorPullsItemsAndShattersRocksIntoStoneOnTheGround() throws Exception {
        GameMap map = map(new int[]{8, 4}, new int[]{13, 4});
        Farmer farmer = farmerAt(map, 7, 4);
        map.at(9, 5).addItem(new CopperItem());
        map.at(11, 4).addItem(new CopperItem()); // distance 4: in range
        map.at(12, 4).addItem(new CopperItem()); // distance 5: out of range

        new ProspectorElixir().drink(farmer, map.at(7, 4));

        assertEquals(2, count(farmer, CopperItem.class));
        assertEquals(1, map.at(12, 4).getItems().size());
        assertEquals(Dirt.class, map.at(8, 4).getGround().getClass());
        assertTrue(map.at(8, 4).getItems().stream().anyMatch(i -> i.getClass() == StoneItem.class));
        assertEquals(Rock.class, map.at(13, 4).getGround().getClass(), "rock out of range is untouched");
    }
}