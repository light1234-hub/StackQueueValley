package quests;


import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.actor.Bat;
import game.quest.*;
import game.terrain.Dirt;
import game.terrain.LivingGroveGround;
import game.terrain.QuestArenaWall;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestManagerTest {
    @Test void acceptsAndChargesOnlyOfferedAffordableContract() throws Exception {
        Req3Fixture f = new Req3Fixture();
        assertEquals(1000, f.farmer.getStatistic(FinanceStatistics.COINS));
        String result = f.manager.accept(f.farmer, f.farm, f.forest);
        assertTrue(result.contains("The Cursed Forest"));
        assertTrue(f.manager.hasActiveQuest());
        assertTrue(f.arenaMap.contains(f.farmer));
        assertEquals(880, f.farmer.getStatistic(FinanceStatistics.COINS));
        assertEquals(3, f.manager.totalObjectives());
        assertTrue(f.arenaMap.at(13, 1).getGround() instanceof QuestClockGround);
        assertTrue(f.manager.accept(f.farmer, f.arenaMap, f.forest).contains("quest board"));
        assertEquals(880, f.farmer.getStatistic(FinanceStatistics.COINS));
    }

    @Test void rejectsUnfundedAndInvalidContractsWithoutSideEffects() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.farmer.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.UPDATE, 0);
        assertTrue(f.manager.accept(f.farmer, f.farm, f.forest).contains("Not enough coins"));
        assertFalse(f.manager.hasActiveQuest());
        assertEquals(0, f.farmer.getStatistic(FinanceStatistics.COINS));
        assertTrue(f.farm.contains(f.farmer));

        f.farmer.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.UPDATE, 1000);
        QuestDefinition notOffered = new QuestDefinition("Not offered", 1, 1, 1, 3,
                false, new CursedForestScenario(), new ForestConsequence());
        assertTrue(f.manager.accept(f.farmer, f.farm, notOffered).contains("does not offer"));
        assertFalse(f.manager.hasActiveQuest());
        assertEquals(1000, f.farmer.getStatistic(FinanceStatistics.COINS));
    }

    @Test void allThreeScenariosPlaceObjectivesAndRequiredTerrain() throws Exception {
        for (int i = 0; i < 3; i++) {
            Req3Fixture f = new Req3Fixture();
            QuestDefinition quest = new QuestDefinition[]{f.forest, f.rescue, f.relic}[i];
            f.manager.accept(f.farmer, f.farm, quest);
            assertTrue(f.manager.hasActiveQuest(), quest.title());
            assertEquals(i == 1 ? 2 : 3, f.manager.totalObjectives());
            assertTrue(f.arenaMap.at(13, 1).getGround() instanceof QuestClockGround);
            int objectiveX = i == 0 ? 7 : 5;
            int objectiveY = i == 0 ? 2 : i == 1 ? 2 : 5;
            assertTrue(f.arenaMap.at(objectiveX, objectiveY).getGround()
                            instanceof QuestObjectiveGround,
                    "Each scenario must place its designated objective");
            assertTrue(f.arenaMap.at(i == 1 ? 11 : 12, i == 0 ? 5 : 2).containsAnActor());
        }
    }

    @Test void failureByAbandonmentReturnsFarmerWithoutRewards() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.manager.accept(f.farmer, f.farm, f.forest);
        String result = f.manager.abandon(f.farmer, f.arenaMap);
        assertTrue(result.contains("failed"));
        assertFalse(f.manager.hasActiveQuest());
        assertTrue(f.farm.contains(f.farmer));
        assertEquals(880, f.farmer.getStatistic(FinanceStatistics.COINS));
        assertEquals(0, f.farmer.getTotalExperience());
        assertTrue(f.arenaMap.at(7, 2).getGround() instanceof Dirt);
    }

    @Test void successAwardsExactCoinsAndExperienceAndResetsArena() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.manager.accept(f.farmer, f.farm, f.forest);
        int[][] objectives = {{7, 2}, {10, 4}, {11, 6}};
        for (int[] xy : objectives) {
            f.positionBeside(xy[0], xy[1]);
            QuestObjectiveGround objective = (QuestObjectiveGround)
                    f.arenaMap.at(xy[0], xy[1]).getGround();
            String result = f.manager.activate(f.farmer, f.arenaMap, objective);
            assertFalse(result.contains("no longer available"));
        }
        assertFalse(f.manager.hasActiveQuest());
        assertTrue(f.farm.contains(f.farmer));
        assertEquals(1160, f.farmer.getStatistic(FinanceStatistics.COINS));
        assertEquals(100, f.farmer.getTotalExperience());
        assertTrue(f.arenaMap.at(7, 2).getGround() instanceof Dirt);
        assertTrue(f.farm.at(0, 3).getGround() instanceof LivingGroveGround);
    }

    @Test void ancientRelicObjectivesMustBeActivatedInOrder() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.manager.accept(f.farmer, f.farm, f.relic);
        f.positionBeside(9, 2);
        QuestObjectiveGround second = (QuestObjectiveGround) f.arenaMap.at(9, 2).getGround();
        String error = f.manager.activate(f.farmer, f.arenaMap, second);
        assertTrue(error.contains("in sequence"));
        assertEquals(0, f.manager.completedObjectives());
        assertTrue(f.arenaMap.at(9, 2).getGround() instanceof QuestObjectiveGround);
        f.positionBeside(5, 5);
        QuestObjectiveGround first = (QuestObjectiveGround) f.arenaMap.at(5, 5).getGround();
        assertTrue(f.manager.activate(f.farmer, f.arenaMap, first).contains("1/3"));
        assertEquals(1, f.manager.completedObjectives());
    }

    @Test void expiryOccursOnlyAfterFullTimeAllowance() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.manager.accept(f.farmer, f.farm, f.forest);
        f.manager.tick(); // first engine update before player action
        assertEquals(45, f.manager.turnsRemaining());
        for (int i = 0; i < 44; i++) f.manager.tick();
        assertTrue(f.manager.hasActiveQuest());
        assertEquals(1, f.manager.turnsRemaining());
        f.manager.tick(); // time has run out
        assertFalse(f.manager.hasActiveQuest());
        assertTrue(f.farm.contains(f.farmer));
        assertEquals(0, f.farmer.getTotalExperience());
    }

    @Test void blockedClockCancelsContractBeforeCharging() throws Exception {
        Req3Fixture f = new Req3Fixture();
        QuestScenario blocked = setup -> {
            setup.addObjective(5, 2, "test objective");
            setup.requireActor(13, 1, new Bat());
        };
        QuestDefinition contract = new QuestDefinition("Clock test", 120, 20, 5,
                5, false, blocked, new ForestConsequence());
        QuestManager manager = new QuestManager(f.farmer, f.arena, f.farm.at(5, 3),
                f.farm.at(5, 4), f.farm, java.util.List.of(contract));
        String result = manager.accept(f.farmer, f.farm, contract);
        assertTrue(result.contains("Cannot prepare"));
        assertFalse(manager.hasActiveQuest());
        assertEquals(1000, f.farmer.getStatistic(FinanceStatistics.COINS));
        assertFalse(f.arenaMap.at(13, 1).containsAnActor());
    }

    @Test void blockedReturnRetriesWithoutDuplicatingConsequences() throws Exception {
        Req3Fixture f = new Req3Fixture();
        f.manager.accept(f.farmer, f.farm, f.forest);
        // Every farm tile becomes impassable after the Farmer leaves it.
        for (int y : f.farm.getYRange()) {
            for (int x : f.farm.getXRange()) f.farm.at(x, y).setGround(new QuestArenaWall());
        }
        f.manager.tick();
        for (int i = 0; i < 45; i++) f.manager.tick();
        assertTrue(f.manager.hasActiveQuest());
        assertTrue(f.arenaMap.contains(f.farmer));
        assertEquals(0, f.farmer.getTotalExperience());
        // A later change opens a safe location; return is retried exactly once.
        f.farm.at(5, 3).setGround(new Dirt());
        f.manager.tick();
        assertFalse(f.manager.hasActiveQuest());
        assertTrue(f.farm.contains(f.farmer));
        assertEquals(880, f.farmer.getStatistic(FinanceStatistics.COINS));
    }
}

