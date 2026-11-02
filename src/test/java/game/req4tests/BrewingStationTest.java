package game.req4tests;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Farmer;
import game.item.CopperItem;
import game.item.GaleDraught;
import game.item.StrawberryItem;
import game.item.TomatoItem;
import game.terrain.BrewingStation;
import game.terrain.OrchardCauldron;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static game.req4tests.TestSupport.*;


import static org.junit.jupiter.api.Assertions.*;

/** Load / brew / collect lifecycle, tested through the OrchardCauldron (Gale Draught, 50 XP, 3 turns). */
class BrewingStationTest {
    private GameMap map;
    private Location cauldronAt;
    private BrewingStation cauldron;
    private Farmer farmer;
    private int base; // the farmer starts with some tools already

    @BeforeEach
    void setUp() throws Exception {
        map = map();
        cauldronAt = map.at(4, 1);
        cauldronAt.setGround(new OrchardCauldron());
        cauldron = station(cauldronAt);
        farmer = farmerAt(map, 4, 2);
        base = farmer.getInventory().getItems().size();
    }

    private void giveGaleIngredients() throws Exception {
        give(farmer, 3, StrawberryItem::new);
        give(farmer, 1, TomatoItem::new);
        give(farmer, 1, CopperItem::new);
    }

    @Test
    void cauldronIsImpassable() throws Exception {
        assertFalse(cauldronAt.canActorEnter(farmer));
    }

    @Test
    void loadFailsWithoutEnoughXp() throws Exception {
        giveGaleIngredients();
        farmer.gainExp(49);
        cauldron.load(farmer, cauldronAt);
        assertTrue(cauldron.isIdle());
        assertEquals(base + 5, farmer.getInventory().getItems().size());
    }

    @Test
    void loadFailsWithMissingIngredients() throws Exception {
        farmer.gainExp(50);
        give(farmer, 2, StrawberryItem::new); // needs 3
        give(farmer, 1, TomatoItem::new);
        give(farmer, 1, CopperItem::new);
        cauldron.load(farmer, cauldronAt);
        assertTrue(cauldron.isIdle());
        assertEquals(base + 4, farmer.getInventory().getItems().size());
    }

    @Test
    void loadConsumesIngredientsAndStartsBrewWithoutSpendingXp() throws Exception {
        giveGaleIngredients();
        farmer.gainExp(50);
        cauldron.load(farmer, cauldronAt);
        assertTrue(cauldron.isBrewing());
        assertEquals(3, cauldron.getTurnsRemaining());
        assertEquals(base, farmer.getInventory().getItems().size());
        assertEquals(50, farmer.getTotalExperience(), "XP is a threshold, never spent");
    }

    @Test
    void brewBecomesReadyAfterBrewTimeAndCannotBeReloadedUntilCollected() throws Exception {
        giveGaleIngredients();
        farmer.gainExp(50);
        cauldron.load(farmer, cauldronAt);
        tick(cauldron, cauldronAt, 2);
        assertTrue(cauldron.isBrewing());
        tick(cauldron, cauldronAt, 1);
        assertTrue(cauldron.isReady());

        giveGaleIngredients();
        cauldron.load(farmer, cauldronAt);
        assertTrue(cauldron.isReady(), "cannot be reloaded while a potion is waiting");
        assertEquals(base + 5, farmer.getInventory().getItems().size());
    }

    @Test
    void collectGivesPotionAndResetsCauldron() throws Exception {
        giveGaleIngredients();
        farmer.gainExp(50);
        cauldron.load(farmer, cauldronAt);
        tick(cauldron, cauldronAt, 3);
        cauldron.collect(farmer, cauldronAt);
        assertEquals(1, count(farmer, GaleDraught.class));
        assertTrue(cauldron.isIdle());
    }

    @Test
    void collectBeforeReadyGivesNothing() throws Exception {
        giveGaleIngredients();
        farmer.gainExp(50);
        cauldron.load(farmer, cauldronAt);
        cauldron.collect(farmer, cauldronAt);
        assertEquals(0, count(farmer, GaleDraught.class));
        assertTrue(cauldron.isBrewing());
    }

    @Test
    void offersLoadWhenIdleAndCollectWhenReadyOnly() throws Exception {
        assertEquals(1, cauldron.allowableActions(farmer, cauldronAt, "north").size());
        giveGaleIngredients();
        farmer.gainExp(50);
        cauldron.load(farmer, cauldronAt);
        assertEquals(0, cauldron.allowableActions(farmer, cauldronAt, "north").size());
        tick(cauldron, cauldronAt, 3);
        assertEquals(1, cauldron.allowableActions(farmer, cauldronAt, "north").size());
    }
}