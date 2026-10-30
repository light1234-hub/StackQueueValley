package game.tests;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.CreatureControl;
import game.actor.Farmer;
import game.actor.Slime;
import game.item.AegisElixir;
import game.item.CopperItem;
import game.item.DiamondItem;
import game.item.Lure;
import game.item.ProspectorElixir;
import game.item.StoneItem;
import game.item.StrawberryItem;
import game.item.TomatoItem;
import game.terrain.ArcaneSapling;
import game.terrain.ArcaneTree;
import game.terrain.BrewingStation;
import game.terrain.Dirt;
import game.terrain.OrchardCauldron;
import game.terrain.QuarryCauldron;
import game.terrain.Rock;
import game.terrain.TamingCauldron;
import game.terrain.TileFinder;
import org.junit.jupiter.api.Test;

import static game.tests.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

/** The in-brew effect of each cauldron and the terrain / items it creates. */
class CauldronEffectsTest {

    private static long saplings(GameMap map) throws Exception {
        return TileFinder.all(map).stream()
                .filter(l -> l.getGround().getClass() == ArcaneSapling.class).count();
    }

    @Test
    void orchardPlantsOneSaplingPerBrewTurn() throws Exception {
        GameMap map = map();
        Location at = map.at(4, 1);
        at.setGround(new OrchardCauldron());
        Farmer farmer = farmerAt(map, 4, 2);
        give(farmer, 3, StrawberryItem::new);
        give(farmer, 1, TomatoItem::new);
        give(farmer, 1, CopperItem::new);
        farmer.gainExp(50);
        BrewingStation s = station(at);
        s.load(farmer, at);
        tick(s, at, 1);
        assertEquals(1, saplings(map));
        tick(s, at, 2);
        assertEquals(3, saplings(map));
        tick(s, at, 1); // READY: no more effect
        assertEquals(3, saplings(map));
    }

    @Test
    void saplingGrowsIntoImpassableTreeAfterThreeTurns() throws Exception {
        GameMap map = map();
        Location l = map.at(6, 6);
        l.setGround(new ArcaneSapling(new Dirt()));
        l.getGround().tick(l);
        l.getGround().tick(l);
        assertEquals(ArcaneSapling.class, l.getGround().getClass());
        l.getGround().tick(l);
        assertEquals(ArcaneTree.class, l.getGround().getClass());
        assertFalse(l.canActorEnter(new Slime()));
    }

    @Test
    void hostileCreatureTramplesSapling() throws Exception {
        GameMap map = map();
        Location l = map.at(6, 6);
        l.setGround(new ArcaneSapling(new Dirt()));
        l.addActor(new Slime());
        l.getGround().tick(l);
        assertEquals(Dirt.class, l.getGround().getClass());
    }

    @Test
    void treeDropsStrawberryOrTomatoEveryTwoTurns() throws Exception {
        GameMap map = map();
        Location l = map.at(6, 6);
        l.setGround(new ArcaneTree());
        l.getGround().tick(l);
        assertTrue(l.getItems().isEmpty());
        l.getGround().tick(l);
        assertEquals(1, l.getItems().size());
        Class<?> type = l.getItems().get(0).getClass();
        assertTrue(type == StrawberryItem.class || type == TomatoItem.class);
    }

    private Farmer loadTaming(GameMap map, Location at) throws Exception {
        Farmer farmer = farmerAt(map, 7, 2);
        give(farmer, 3, StoneItem::new);
        give(farmer, 2, StrawberryItem::new);
        give(farmer, 1, DiamondItem::new);
        farmer.gainExp(100);
        station(at).load(farmer, at);
        return farmer;
    }

    @Test
    void tamingPlacesOneLurePerTurnAndRemovesUnusedOnesOnCollect() throws Exception {
        GameMap map = map();
        Location at = map.at(7, 1);
        at.setGround(new TamingCauldron());
        Farmer farmer = loadTaming(map, at);
        BrewingStation s = station(at);
        tick(s, at, 2);
        assertEquals(2, lures(map));
        tick(s, at, 2);
        assertTrue(s.isReady());
        assertEquals(4, lures(map));
        s.collect(farmer, at);
        assertEquals(0, lures(map));
        assertEquals(1, count(farmer, AegisElixir.class));
    }

    @Test
    void creatureReachingLureUsesItUpAndBecomesTamed() throws Exception {
        GameMap map = map();
        Location l = map.at(8, 4);
        Slime slime = new Slime();
        l.addActor(slime);
        l.addItem(new Lure());
        CreatureControl.installLureSeeking(slime, map);
        slime.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, map,
                new edu.monash.fit2099.engine.displays.Display());
        assertTrue(CreatureControl.isTamed(slime));
        assertEquals(0, lures(map));
    }

    @Test
    void quarryGolemWalksToRockAndLeavesStoneOnThatTileThenCrumbles() throws Exception {
        GameMap map = map(new int[]{5, 3});
        Location at = map.at(4, 1);
        at.setGround(new QuarryCauldron());
        Farmer farmer = farmerAt(map, 3, 0);
        give(farmer, 2, CopperItem::new);
        give(farmer, 2, TomatoItem::new);
        give(farmer, 1, StoneItem::new);
        farmer.gainExp(150);
        BrewingStation s = station(at);
        s.load(farmer, at);
        long actorsWithGolem = TileFinder.all(map).stream().filter(Location::containsAnActor).count();
        assertEquals(2, actorsWithGolem, "farmer + golem");

        tick(s, at, 5);
        Location rock = map.at(5, 3);
        assertNotEquals(Rock.class, rock.getGround().getClass(), "rock was shattered");
        assertTrue(rock.getItems().stream().anyMatch(i -> i.getClass() == StoneItem.class),
                "stone lies on the rock's own tile");
        assertEquals(0, count(farmer, StoneItem.class), "farmer must pick the stone up himself");

        s.collect(farmer, at);
        assertEquals(1, count(farmer, ProspectorElixir.class));
        long actorsAfter = TileFinder.all(map).stream().filter(Location::containsAnActor).count();
        assertEquals(1, actorsAfter, "golem crumbled");
    }
}