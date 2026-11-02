package quests;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.quest.ForestConsequence;
import game.quest.QuestWorldContext;
import game.quest.RelicConsequence;
import game.quest.RescueConsequence;
import game.questboard.DiceChoice;
import game.questboard.DiceWager;
import game.terrain.LivingGroveGround;
import game.terrain.ResonantCrystalGround;
import game.terrain.SpreadingBlightGround;
import game.terrain.SurvivorCampGround;
import game.terrain.UnstableGround;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestDiceAndConsequencesTest {
    @Test void diceCannotStartWhenContractIsAffordable() throws Exception {
        Req3Fixture f = new Req3Fixture();
        DiceWager wager = new DiceWager(f.manager, Req3Fixture.fixedFaces(6, 6, 6));
        assertFalse(wager.canChallenge(f.farmer, f.farm, f.forest));
        assertTrue(wager.start(f.farmer, f.farm, f.forest).contains("unavailable"));
        assertFalse(wager.inProgress());
    }

    @Test void safeDiceRollsWinFreeEntryWithoutChargingCoins() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.farmer.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.UPDATE, 0);
        DiceWager wager = new DiceWager(f.manager, Req3Fixture.fixedFaces(6, 6, 6));
        assertTrue(wager.canChallenge(f.farmer, f.farm, f.forest));
        wager.start(f.farmer, f.farm, f.forest);
        assertTrue(wager.play(f.farmer, f.farm, DiceChoice.SAFE).contains("Round 1/3"));
        assertTrue(wager.play(f.farmer, f.farm, DiceChoice.SAFE).contains("Round 2/3"));
        assertTrue(wager.play(f.farmer, f.farm, DiceChoice.SAFE).contains("won free entry"));
        assertTrue(f.manager.hasActiveQuest());
        assertEquals(0, f.farmer.getStatistic(FinanceStatistics.COINS));
        assertFalse(wager.inProgress());
    }

    @Test void riskyBustCostsStaminaAndStartsCooldown() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.farmer.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.UPDATE, 0);
        DiceWager wager = new DiceWager(f.manager, Req3Fixture.fixedFaces(1));
        wager.start(f.farmer, f.farm, f.forest);
        int stamina = f.farmer.getStatistic(ActorStatistics.STAMINA);
        assertTrue(wager.play(f.farmer, f.farm, DiceChoice.RISKY).contains("bust"));
        assertEquals(stamina - 4, f.farmer.getStatistic(ActorStatistics.STAMINA));
        assertEquals(5, wager.cooldownTurns());
        assertFalse(wager.inProgress());
        assertFalse(f.manager.hasActiveQuest());
        for (int i = 0; i < 5; i++) wager.tick();
        assertEquals(0, wager.cooldownTurns());
        assertTrue(wager.canChallenge(f.farmer, f.farm, f.forest));
    }

    @Test void safeLowScoreAndForfeitBothLoseWithoutStartingQuest() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.farmer.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.UPDATE, 0);
        DiceWager low = new DiceWager(f.manager, Req3Fixture.fixedFaces(1, 1, 1));
        low.start(f.farmer, f.farm, f.forest);
        for (int i = 0; i < 2; i++) low.play(f.farmer, f.farm, DiceChoice.SAFE);
        assertTrue(low.play(f.farmer, f.farm, DiceChoice.SAFE).contains("Not enough points"));
        assertFalse(f.manager.hasActiveQuest());
        DiceWager forfeit = new DiceWager(f.manager, Req3Fixture.fixedFaces());
        forfeit.start(f.farmer, f.farm, f.forest);
        assertTrue(forfeit.play(f.farmer, f.farm, DiceChoice.FORFEIT).contains("forfeited"));
        assertEquals(5, forfeit.cooldownTurns());
        assertFalse(f.manager.hasActiveQuest());
    }

    @Test void allSixConsequencesModifyFarmState() throws Exception {
        // Fresh fixtures prevent one feature's terrain from influencing another.
        Req3Fixture forestSuccess = new Req3Fixture();
        assertTrue(new ForestConsequence().onSuccess(
                new QuestWorldContext(forestSuccess.farm)).contains("Living Grove"));
        assertTrue(forestSuccess.farm.at(0, 3).getGround() instanceof LivingGroveGround);

        Req3Fixture forestFailure = new Req3Fixture();
        assertTrue(new ForestConsequence().onFailure(
                new QuestWorldContext(forestFailure.farm)).contains("Spreading Blight"));
        assertTrue(forestFailure.farm.at(0, 3).getGround() instanceof SpreadingBlightGround);

        Req3Fixture rescueSuccess = new Req3Fixture();
        assertTrue(new RescueConsequence().onSuccess(
                new QuestWorldContext(rescueSuccess.farm)).contains("survivor camp"));
        assertTrue(rescueSuccess.farm.at(2, 4).getGround() instanceof SurvivorCampGround);
        assertTrue(rescueSuccess.farm.at(7, 5).containsAnActor());

        Req3Fixture rescueFailure = new Req3Fixture();
        assertTrue(new RescueConsequence().onFailure(
                new QuestWorldContext(rescueFailure.farm)).contains("scavenger"));
        assertTrue(rescueFailure.farm.at(7, 5).containsAnActor());

        Req3Fixture relicSuccess = new Req3Fixture();
        assertTrue(new RelicConsequence().onSuccess(
                new QuestWorldContext(relicSuccess.farm)).contains("resonant vein"));
        assertTrue(relicSuccess.farm.at(3, 4).getGround() instanceof ResonantCrystalGround);

        Req3Fixture relicFailure = new Req3Fixture();
        assertTrue(new RelicConsequence().onFailure(
                new QuestWorldContext(relicFailure.farm)).contains("unstable ground"));
        assertTrue(relicFailure.farm.at(8, 0).getGround() instanceof UnstableGround);
    }
}

