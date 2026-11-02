package game.req3tests;


import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.Farmer;
import game.actor.FinanceStatistics;
import game.arena.ArenaQuest;
import game.terrain.Dirt;
import java.util.List;
import java.util.Random;

/** Test-only construction of small, isolated maps; no game loop or network needed. */
final class Req3Fixture {
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

