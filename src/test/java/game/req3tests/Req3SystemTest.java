package game.req3tests;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.Bat;
import game.actor.Farmer;
import game.actor.FinanceStatistics;
import game.arena.ArenaQuest;
import game.questboard.DiceChoice;
import game.questboard.DiceWager;
import game.terrain.Dirt;
import game.terrain.LivingGroveGround;
import game.terrain.QuestArenaWall;
import game.terrain.ResonantCrystalGround;
import game.terrain.SpreadingBlightGround;
import game.terrain.SurvivorCampGround;
import game.terrain.UnstableGround;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * REQ3 quest-system unit tests.
 * Covers normal, boundary, and invalid inputs, three scenarios,
 * free-entry wagers, timeouts, and persistent consequences.
 * All fixtures are local and deterministic.
 */
public class Req3SystemTest {

    private QuestDefinition definition(int limit, boolean ordered) {
        QuestScenario scenario = setup -> {};
        QuestConsequence consequence = new QuestConsequence() {
            public String onSuccess(QuestWorldContext world) { return "ok"; }
            public String onFailure(QuestWorldContext world) { return "failed"; }
        };
        return new QuestDefinition("Unit-test contract", 10, 20, 30, limit,
                ordered, scenario, consequence);
    }

    @Test void invalidContractsAreRejected() {
        QuestDefinition valid = definition(3, true);
        assertEquals(3, valid.turnLimit());
        assertThrows(IllegalArgumentException.class,
                () -> new QuestDefinition(" ", 10, 20, 30, 3, false,
                        valid.scenario(), valid.consequence()));
        assertThrows(IllegalArgumentException.class,
                () -> new QuestDefinition("Bad fee", -1, 20, 30, 3, false,
                        valid.scenario(), valid.consequence()));
        assertThrows(IllegalArgumentException.class, () -> definition(0, false));
    }

    @Test void deadlineAllowsAllAdvertisedTurns() {
        for (int limit : new int[]{1, 3, 45}) {
            QuestSession session = new QuestSession(definition(limit, false));
            assertEquals(limit, session.turnsRemaining());
            session.tick(); // engine's initial arena tick: no playable action yet
            assertEquals(limit, session.turnsRemaining());
            for (int i = 1; i < limit; i++) {
                session.tick();
                assertEquals(limit - i, session.turnsRemaining());
                assertFalse(session.hasExpired());
            }
            session.tick();
            assertEquals(0, session.turnsRemaining());
            assertTrue(session.hasExpired());
            session.tick();
            assertEquals(0, session.turnsRemaining(), "Timer may not go negative");
        }
    }

    @Test void orderedAndUnorderedObjectivesBehaveDifferently() {
        QuestObjectiveGround first = new QuestObjectiveGround(null, null, "First");
        QuestObjectiveGround second = new QuestObjectiveGround(null, null, "Second");
        QuestSession ordered = new QuestSession(definition(5, true));
        ordered.addObjective(first);
        ordered.addObjective(second);
        assertEquals(2, ordered.totalObjectives());
        assertTrue(ordered.contains(first));
        assertFalse(ordered.isNextObjective(second));
        assertTrue(ordered.isNextObjective(first));
        ordered.completeObjective();
        assertTrue(ordered.isNextObjective(second));
        assertTrue(ordered.isFinalObjective());
        ordered.completeObjective();
        assertTrue(ordered.isComplete());
        assertThrows(IllegalStateException.class, ordered::completeObjective);

        QuestSession unordered = new QuestSession(definition(5, false));
        unordered.addObjective(first);
        unordered.addObjective(second);
        assertTrue(unordered.isNextObjective(second));
    }


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
                f.farm.at(5, 4), f.farm, List.of(contract));
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


    /** Shared deterministic REQ3 fixture, without live API calls. */
    private static final class Req3Fixture {
    final Farmer farmer = new Farmer("Test",null);
    final GameMap farm;
    final GameMap arenaMap;
    final ArenaQuest arena;
    final QuestManager manager;
    final QuestDefinition forest;
    final QuestDefinition rescue;
    final QuestDefinition relic;

    Req3Fixture() throws GameEngineException {
        farm = newMap("Test farm", 15, 10);
        arenaMap = newMap("Test arena", 15, 9);
        World world = new World(new Display()) {};
        world.addGameMap(farm);
        world.addGameMap(arenaMap);
        farm.addActor(farmer, farm.at(5, 3));
        arena = new ArenaQuest(arenaMap, farmer);
        forest = new QuestDefinition("The Cursed Forest", 120, 280, 100, 45,
                false, new CursedForestScenario(), new ForestConsequence());
        rescue = new QuestDefinition("The Lost Survivors", 200, 450, 160, 55,
                false, new RescueMissionScenario(), new RescueConsequence());
        relic = new QuestDefinition("The Ancient Relic", 350, 800, 260, 65,
                true, new AncientRelicScenario(), new RelicConsequence());
        manager = new QuestManager(farmer, arena, farm.at(5, 3), farm.at(5, 4),
                farm, List.of(forest, rescue, relic));
        farmer.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.UPDATE, 1000);
    }

    static GameMap newMap(String name, int width, int height) throws GameEngineException {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Dirt::new);
        return new GameMap(name, creator, '.', width, height);
    }

    void positionBeside(int x, int y) throws GameEngineException {
        arenaMap.moveActor(farmer, arenaMap.at(x - 1, y));
    }

    static Random fixedFaces(int... faces) {
        return new Random(0) {
            private int index;
            @Override public int nextInt(int bound) {
                if (index >= faces.length) throw new AssertionError("Not enough dice values");
                int face = faces[index++];
                if (face < 1 || face > bound) throw new AssertionError("Invalid die face " + face);
                return face - 1;
            }
        };
    }

    }

}
