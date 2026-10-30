package game.tests;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actor.Farmer;
import game.actor.FinanceStatistics;
import game.actor.Wizard;
import game.farmeraction.RecipeInfoAction;
import game.farmeraction.WishAction;
import game.item.GaleDraught;
import game.terrain.WishingWell;
import org.junit.jupiter.api.Test;

import static game.tests.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class WishingWellAndWizardTest {

    @Test
    void wishCostsCoinsAndTeleportsFarmer() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 1, 2);
        int before = farmer.getStatistic(FinanceStatistics.COINS);
        addCoins(farmer, 100);
        new WishAction(map.at(12, 7), 100, "north").execute(farmer, map);
        assertSame(farmer, map.at(12, 7).getActor());
        assertEquals(before, farmer.getStatistic(FinanceStatistics.COINS));
    }

    @Test
    void wishFailsWhenFarmerCannotAfford() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 1, 2);
        int before = farmer.getStatistic(FinanceStatistics.COINS);
        if (before >= 100) {
            farmer.modifyStatistic(FinanceStatistics.COINS,
                    edu.monash.fit2099.engine.statistics.StatisticOperations.DECREASE, before);
            before = 0;
        }
        new WishAction(map.at(12, 7), 100, "north").execute(farmer, map);
        assertSame(farmer, map.at(1, 2).getActor());
        assertEquals(before, farmer.getStatistic(FinanceStatistics.COINS));
    }

    @Test
    void wishFailsWhenArrivalSpotIsOccupied() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 1, 2);
        addCoins(farmer, 100);
        map.at(12, 7).addActor(new Wizard());
        new WishAction(map.at(12, 7), 100, "north").execute(farmer, map);
        assertSame(farmer, map.at(1, 2).getActor());
    }

    @Test
    void wellIsImpassableAndOffersWishOnlyOnceLinked() throws Exception {
        GameMap map = map();
        WishingWell well = new WishingWell(100);
        map.at(1, 1).setGround(well);
        Farmer farmer = farmerAt(map, 1, 2);
        assertFalse(map.at(1, 1).canActorEnter(farmer));
        assertEquals(0, well.allowableActions(farmer, map.at(1, 1), "north").size());
        well.setDestination(map.at(12, 7));
        assertEquals(1, well.allowableActions(farmer, map.at(1, 1), "north").size());
    }

    @Test
    void wizardOffersOneRecipeActionPerPotion() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 7, 4);
        Wizard wizard = new Wizard();
        map.at(7, 5).addActor(wizard);
        ActionList actions = wizard.allowableActions(farmer, "south", map);
        assertEquals(3, actions.size());
    }

    @Test
    void wizardReportsRecipeAndMissingXp() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 7, 4);
        farmer.gainExp(20);
        String text = new RecipeInfoAction(new Wizard(), new GaleDraught()).execute(farmer, map);
        assertTrue(text.contains("3 x Strawberry"));
        assertTrue(text.contains("50"));
        assertTrue(text.contains("3 turns"));
        assertTrue(text.contains("30"), "50 required - 20 earned = 30 missing");
    }

    @Test
    void wizardReportsZeroMissingXpWhenFarmerHasEnough() throws Exception {
        GameMap map = map();
        Farmer farmer = farmerAt(map, 7, 4);
        farmer.gainExp(80);
        String text = new RecipeInfoAction(new Wizard(), new GaleDraught()).execute(farmer, map);
        assertTrue(text.contains("missing 0 XP"));
    }
}