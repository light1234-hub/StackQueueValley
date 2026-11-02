package game.req5tests;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.ability.WeatherEventAbility;
import game.actor.Chicken;
import game.item.CopperItem;
import game.item.Egg;
import game.item.StoneItem;
import game.terrain.CookingStation;
import game.terrain.Dirt;
import game.terrain.Soil;
import game.weather.*;
import game.weatherapi.OpenMeteoProvider;
import game.weatherapi.WeatherDataException;
import game.weatherapi.WeatherDataProvider;
import game.weatherterrain.FallenTreeGround;
import game.weatherterrain.FloodedGround;
import game.weatherterrain.MudGround;
import game.weatherterrain.ScorchedGround;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * REQ5 deterministic JUnit regression suite for weather events, weather
 * responses, crafting, activation modes and API orchestration.
 * Uses a fake WeatherDataProvider rather than a live network request.
 */
public class Req5WeatherTest {

    @Test
    void rainFloodsDirtAndSoilButProtectsResponseStructures() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location dirt = f.at(1, 1);
        Location soil = f.at(2, 1);
        Location drain = f.at(3, 1);
        Location shelter = f.at(4, 1);
        Location recovery = f.at(5, 1);
        soil.setGround(new Soil());
        drain.setGround(new DrainageStructure());
        shelter.setGround(new Shelter());
        recovery.setGround(new ResourceRecoveryStructure());

        new RainstormEvent().apply(f.map, Set.of(dirt, soil, drain, shelter, recovery), new WeatherContext());
        assertInstanceOf(FloodedGround.class, dirt.getGround());
        assertInstanceOf(FloodedGround.class, soil.getGround());
        assertInstanceOf(DrainageStructure.class, drain.getGround());
        assertInstanceOf(Shelter.class, shelter.getGround());
        assertInstanceOf(ResourceRecoveryStructure.class, recovery.getGround());
    }

    @Test
    void cookingStationFloodsByDesign() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location location = f.at(2, 2);
        Ground station = new CookingStation(List.of());
        location.setGround(station);
        new RainstormEvent().apply(f.map, Set.of(location), new WeatherContext());
        FloodedGround flood = assertInstanceOf(FloodedGround.class, location.getGround());
        assertSame(station, flood.getOriginalGround());
    }

    @Test
    void rainAppliesSoakedStatusToActorOnFloodedTile() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Actor actor = f.actorAt(2, 2);
        new RainstormEvent().apply(f.map, Set.of(f.at(2, 2)), new WeatherContext());
        assertTrue(actor.hasStatus(SoakedStatus.class));
        assertInstanceOf(FloodedGround.class, f.at(2, 2).getGround());
    }

    @Test
    void repeatedRainDoesNotReplaceAlreadyFloodedGround() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location target = f.at(2, 2);
        RainstormEvent rain = new RainstormEvent();
        rain.apply(f.map, Set.of(target), new WeatherContext());
        Ground first = target.getGround();
        rain.apply(f.map, Set.of(target), new WeatherContext());
        assertSame(first, target.getGround());
    }

    @Test
    void heatwaveScorchesSoilButNotCookingStation() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        f.at(1, 1).setGround(new Soil());
        f.at(2, 1).setGround(new CookingStation(List.of()));
        new HeatwaveEvent().apply(f.map, Set.of(f.at(1, 1), f.at(2, 1)), new WeatherContext());
        assertInstanceOf(ScorchedGround.class, f.at(1, 1).getGround());
        assertInstanceOf(CookingStation.class, f.at(2, 1).getGround());
    }

    @Test
    void tornadoCreatesRecoverableObstaclesAndPreservesWeatherStructures() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        f.at(2, 2).setGround(new DrainageStructure());
        new TornadoEvent().apply(f.map, Set.of(f.at(1, 1), f.at(2, 2)), new WeatherContext());
        assertInstanceOf(FallenTreeGround.class, f.at(1, 1).getGround());
        assertInstanceOf(DrainageStructure.class, f.at(2, 2).getGround());
    }

    @Test
    void tornadoScattersItemsToAdjacentLocation() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location location = f.at(4, 4);
        StoneItem stone = new StoneItem();
        location.addItem(stone);
        new TornadoEvent().apply(f.map, Set.of(location), new WeatherContext());
        assertFalse(location.getItems().contains(stone));
        assertTrue(location.getExits().stream()
                .anyMatch(exit -> exit.getDestination().getItems().contains(stone)));
    }


    @Test
    void drainageImmediatelyRestoresOriginalGround() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location floodTile = f.at(3, 3);
        Ground original = new Dirt();
        floodTile.setGround(new FloodedGround(original));
        new DrainageResponse().respond(f.at(4, 4));
        assertSame(original, floodTile.getGround(),
                "Drainage must restore the original ground immediately, without Mud");
    }

    @Test
    void drainageRestoresFloodedGroundEvenWhenOccupied() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location target = f.at(3, 3);
        Dirt original = new Dirt();
        target.setGround(new FloodedGround(original));
        Actor occupant = f.actorAt(3, 3);
        new DrainageResponse().respond(f.at(4, 4));
        assertSame(original, target.getGround());
        assertSame(target, f.map.locationOf(occupant));
    }

    @Test
    void drainageRestoresAllTilesWithinRadiusAndLeavesDistantFloods() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture(15, 15);
        Ground dirt = new Dirt();
        Ground soil = new Soil();
        Ground edge = new Dirt();
        f.at(5, 4).setGround(new FloodedGround(dirt));
        f.at(7, 7).setGround(new FloodedGround(soil));
        f.at(10, 5).setGround(new FloodedGround(edge)); // Exactly 5 tiles away
        f.at(11, 5).setGround(new FloodedGround(new Dirt())); // 6 away
        new DrainageResponse().respond(f.at(5, 5));
        assertSame(dirt, f.at(5, 4).getGround());
        assertSame(soil, f.at(7, 7).getGround());
        assertSame(edge, f.at(10, 5).getGround());
        assertInstanceOf(FloodedGround.class, f.at(11, 5).getGround());
    }

    @Test
    void wildlifeShelterRelocatesChickenAndProducesEgg() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Chicken chicken = new Chicken();
        Location danger = f.at(4, 4);
        danger.setGround(new FloodedGround(new Dirt()));
        f.map.addActor(chicken, danger);
        new ShelterResponse().respond(f.at(5, 5));
        Location destination = f.map.locationOf(chicken);
        assertNotSame(danger, destination);
        assertFalse(destination.getGround() instanceof FloodedGround);
        assertTrue(destination.getItems().stream().anyMatch(Egg.class::isInstance));
    }

    @Test
    void wildlifeShelterDoesNothingForSafeChicken() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Chicken chicken = new Chicken();
        f.map.addActor(chicken, f.at(4, 4));
        new ShelterResponse().respond(f.at(5, 5));
        assertSame(f.at(4, 4), f.map.locationOf(chicken));
    }

    @Test
    void resourceRecoveryClearsTreeAndProducesEitherFiveCopperOrTenStone() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location target = f.at(3, 3);
        target.setGround(new FallenTreeGround());
        new ResourceRecoveryResponse().respond(f.at(4, 4));
        assertInstanceOf(Dirt.class, target.getGround());
        long copper = target.getItems().stream().filter(CopperItem.class::isInstance).count();
        long stone = target.getItems().stream().filter(StoneItem.class::isInstance).count();
        assertTrue((copper == 5 && stone == 0) || (copper == 0 && stone == 10),
                "A recovered tree must drop exactly one of the two configured rewards");
    }

    @Test
    void recoverySkipsOccupiedTree() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location target = f.at(3, 3);
        target.setGround(new FallenTreeGround());
        // Place actor on the tile before converting it into impassable terrain.
        // The recovery contract should reject an occupied tile.
        Actor actor = f.actorAt(3, 3);
        new ResourceRecoveryResponse().respond(f.at(4, 4));
        assertInstanceOf(FallenTreeGround.class, target.getGround());
        assertSame(target, f.map.locationOf(actor));
    }

    @Test
    void drainageRunsOnPlacementNotOnLaterTicks() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Location target = f.at(4, 4);
        DrainageStructure structure = new DrainageStructure();
        target.setGround(structure);
        Soil first = new Soil();
        f.at(3, 3).setGround(new FloodedGround(first));
        structure.onPlaced(target);
        assertSame(first, f.at(3, 3).getGround());
        f.at(5, 5).setGround(new FloodedGround(new Dirt()));
        structure.tick(target);
        assertInstanceOf(FloodedGround.class, f.at(5, 5).getGround());
    }

    @Test
    void placingDrainageKitDrainsThreeFloodsImmediately() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Actor actor = f.actorAt(4, 4);
        WeatherStructureKit kit = new WeatherStructureKit(drainage());
        actor.getInventory().add(kit);
        Dirt originalA = new Dirt();
        Soil originalB = new Soil();
        Dirt originalC = new Dirt();
        f.at(3, 3).setGround(new FloodedGround(originalA));
        f.at(5, 5).setGround(new FloodedGround(originalB));
        f.at(6, 4).setGround(new FloodedGround(originalC));
        f.at(5, 4).setGround(new Soil());
        new PlaceWeatherStructureAction(kit, f.at(5, 4), "East")
                .execute(actor, f.map);
        assertInstanceOf(DrainageStructure.class, f.at(5, 4).getGround());
        assertSame(originalA, f.at(3, 3).getGround());
        assertSame(originalB, f.at(5, 5).getGround());
        assertSame(originalC, f.at(6, 4).getGround());
        assertFalse(actor.getInventory().getItems().contains(kit));
    }


    private static WeatherSnapshot snapshot(double rain) {
        return new WeatherSnapshot(25, rain, 2, 50, Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void mapperConvertsDifferentGameTilesIntoDifferentCoordinates() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        WeatherLocationMapper mapper = new WeatherLocationMapper(3.0, 101.0, 0.01, 0.02);
        GeographicCoordinate origin = mapper.toCoordinate(f.at(0, 0));
        GeographicCoordinate other = mapper.toCoordinate(f.at(2, 3));
        assertEquals(3.0, origin.getLatitude(), 0.0000001);
        assertEquals(101.0, origin.getLongitude(), 0.0000001);
        assertEquals(2.97, other.getLatitude(), 0.0000001);
        assertEquals(101.04, other.getLongitude(), 0.0000001);
    }

    @Test
    void weatherConditionsUseInclusiveThresholds() {
        WeatherConditions rain = new WeatherConditions(WeatherEventAbility.RAINSTORM, 1,
                1.0, null, null, null);
        assertFalse(rain.isContributedBy(new TileWeather(25, 0.99, 0, 40)));
        assertTrue(rain.isContributedBy(new TileWeather(25, 1.0, 0, 40)));
        assertTrue(rain.isContributedBy(new TileWeather(25, 2.0, 0, 40)));
    }

    @Test
    void evaluatorRequiresMinimumNumberOfContributingTiles() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        WeatherEvaluator evaluator = new WeatherEvaluator(List.of(new WeatherConditions(
                WeatherEventAbility.RAINSTORM, 2, 1.0, null, null, null)));
        Map<Location, TileWeather> conditions = Map.of(
                f.at(1, 1), new TileWeather(20, 1, 0, 0),
                f.at(1, 2), new TileWeather(20, 0, 0, 0));
        assertTrue(evaluator.evaluate(conditions).isEmpty());
        conditions = Map.of(
                f.at(1, 1), new TileWeather(20, 1, 0, 0),
                f.at(1, 2), new TileWeather(20, 2, 0, 0));
        assertEquals(WeatherEventAbility.RAINSTORM, evaluator.evaluate(conditions).orElseThrow());
    }

    @Test
    void coordinatorFetchesImmediatelyThenEveryFiveTurns() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        AtomicInteger fetches = new AtomicInteger();
        WeatherDataProvider fake = (lat, lon) -> {
            fetches.incrementAndGet();
            return snapshot(0);
        };
        WeatherCoordinator coordinator = coordinator(f, fake, 5);
        coordinator.updateOnTurn(f.at(2, 2));
        assertEquals(1, fetches.get());
        for (int i = 0; i < 4; i++) coordinator.updateOnTurn(f.at(2, 2));
        assertEquals(1, fetches.get());
        coordinator.updateOnTurn(f.at(2, 2));
        assertEquals(2, fetches.get());
    }

    @Test
    void coordinatorDoesNotFetchForDifferentMap() throws Exception {
        WeatherTestFixture original = new WeatherTestFixture();
        WeatherTestFixture elsewhere = new WeatherTestFixture();
        AtomicInteger fetches = new AtomicInteger();
        WeatherCoordinator coordinator = coordinator(original, (lat, lon) -> {
            fetches.incrementAndGet();
            return snapshot(0);
        }, 5);
        coordinator.updateOnTurn(elsewhere.at(1, 1));
        assertEquals(0, fetches.get());
    }

    @Test
    void coordinatorHandlesApiExceptionsWithoutThrowing() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        WeatherCoordinator coordinator = coordinator(f,
                (lat, lon) -> { throw new WeatherDataException("Simulated network failure"); }, 1);
        ByteArrayOutputStream capturedError = new ByteArrayOutputStream();
        PrintStream originalError = System.err;
        PrintStream redirectedError = new PrintStream(capturedError);
        try {
            System.setErr(redirectedError);
            assertDoesNotThrow(() -> coordinator.updateOnTurn(f.at(2, 2)));
        } finally {
            System.setErr(originalError);
            redirectedError.close();
        }
        assertTrue(capturedError.toString().contains(
                "Weather API Error: Simulated network failure"));
    }

    @Test
    void coordinatorRejectsInvalidRefreshIntervalOrRadius() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        assertThrows(IllegalArgumentException.class, () -> coordinator(f, (lat, lon) -> snapshot(0), 0));
        assertThrows(IllegalArgumentException.class, () -> new WeatherCoordinator(
                f.map, (lat, lon) -> snapshot(0), new WeatherLocationMapper(3, 101, 0.01, 0.01),
                new TileWeatherGenerator(), new WeatherEvaluator(List.of()),
                new EnumMap<>(WeatherEventAbility.class), new WeatherContext(), 1, -1));
    }

    @Test
    void providerRejectsOutOfRangeCoordinatesBeforeHttpCall() {
        OpenMeteoProvider provider = new OpenMeteoProvider();
        assertThrows(IllegalArgumentException.class, () -> provider.fetch(91, 100));
        assertThrows(IllegalArgumentException.class, () -> provider.fetch(3, -181));
    }

    @Test
    void coordinatorPassesCurrentGameCoordinatesToProvider() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        AtomicReference<Double> latitude = new AtomicReference<>();
        AtomicReference<Double> longitude = new AtomicReference<>();
        WeatherCoordinator coordinator = coordinator(f, (lat, lon) -> {
            latitude.set(lat);
            longitude.set(lon);
            return snapshot(0);
        }, 1);
        coordinator.updateOnTurn(f.at(2, 3));
        assertEquals(2.97, latitude.get(), 0.000001);
        assertEquals(101.04, longitude.get(), 0.000001);
        coordinator.updateOnTurn(f.at(3, 4));
        assertEquals(2.96, latitude.get(), 0.000001);
        assertEquals(101.06, longitude.get(), 0.000001);
    }

    @Test
    void qualifyingApiWeatherTriggersEventInRadiusOnly() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        AtomicInteger calls = new AtomicInteger();
        AtomicInteger affected = new AtomicInteger();
        WeatherEvent spy = new WeatherEvent() {
            @Override public WeatherEventAbility getType() { return WeatherEventAbility.RAINSTORM; }
            @Override public void apply(GameMap map, Set<Location> locations, WeatherContext context) {
                calls.incrementAndGet();
                affected.set(locations.size());
                assertTrue(locations.contains(f.at(4, 4)));
                assertFalse(locations.contains(f.at(8, 8)));
            }
        };
        Map<WeatherEventAbility, WeatherEvent> events = new EnumMap<>(WeatherEventAbility.class);
        events.put(WeatherEventAbility.RAINSTORM, spy);
        WeatherCoordinator coordinator = new WeatherCoordinator(f.map, (lat, lon) -> snapshot(5),
                new WeatherLocationMapper(3, 101, 0.01, 0.02), new TileWeatherGenerator(),
                new WeatherEvaluator(List.of(new WeatherConditions(
                        WeatherEventAbility.RAINSTORM, 1, 1.0, null, null, null))),
                events, new WeatherContext(), 1, 2);
        coordinator.updateOnTurn(f.at(4, 4));
        assertEquals(1, calls.get());
        assertEquals(13, affected.get()); // integer grid points within radius 2
    }

    private WeatherCoordinator coordinator(WeatherTestFixture f, WeatherDataProvider provider,
                                            int refreshInterval) {
        return new WeatherCoordinator(f.map, provider,
                new WeatherLocationMapper(3, 101, 0.01, 0.02),
                new TileWeatherGenerator(), new WeatherEvaluator(List.of()),
                new EnumMap<>(WeatherEventAbility.class), new WeatherContext(),
                refreshInterval, 2);
    }


    private static WeatherStructurePlan drainage() {
        return new WeatherStructurePlan("Drainage Structure", 'd',
                WeatherStructureType.DRAINAGE, 2, 1);
    }

    @Test
    void invalidRecipeQuantitiesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new WeatherStructurePlan(
                "Invalid", 'x', WeatherStructureType.DRAINAGE, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> new WeatherStructurePlan(
                "Invalid", 'x', WeatherStructureType.DRAINAGE, 0, 0));
    }

    @Test
    void recipeRejectsInsufficientMaterialsWithoutConsumingAny() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Actor actor = f.actorAt(4, 4);
        StoneItem onlyStone = new StoneItem();
        actor.getInventory().add(onlyStone);
        assertFalse(drainage().canCraft(actor));
        String message = new CraftWeatherStructureAction(drainage()).execute(actor, f.map);
        assertTrue(message.contains("Not enough materials"));
        assertTrue(actor.getInventory().getItems().contains(onlyStone));
        assertEquals(0L, actor.getInventory().getItems().stream()
                .filter(WeatherStructureKit.class::isInstance).count());
    }

    @Test
    void craftingConsumesExactMaterialsAndProducesOneKit() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Actor actor = f.actorAt(4, 4);
        for (int i = 0; i < 3; i++) actor.getInventory().add(new StoneItem());
        for (int i = 0; i < 2; i++) actor.getInventory().add(new CopperItem());
        assertTrue(drainage().canCraft(actor));
        new CraftWeatherStructureAction(drainage()).execute(actor, f.map);
        assertEquals(1L, actor.getInventory().getItems().stream()
                .filter(StoneItem.class::isInstance).count());
        assertEquals(1L, actor.getInventory().getItems().stream()
                .filter(CopperItem.class::isInstance).count());
        assertEquals(1L, actor.getInventory().getItems().stream()
                .filter(WeatherStructureKit.class::isInstance).count());
    }

    @Test
    void allThreePlansCreateCorrespondingStructureTypes() {
        assertInstanceOf(DrainageStructure.class, drainage().createGround());
        assertInstanceOf(Shelter.class, new WeatherStructurePlan(
                "Shelter", 'h', WeatherStructureType.WILDLIFE, 3, 1).createGround());
        assertInstanceOf(ResourceRecoveryStructure.class, new WeatherStructurePlan(
                "Recovery", 'r', WeatherStructureType.RECOVERY, 1, 2).createGround());
    }

    @Test
    void placementConsumesKitAndBuildsOnAdjacentSoil() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Actor actor = f.actorAt(4, 4);
        WeatherStructureKit kit = new WeatherStructureKit(drainage());
        actor.getInventory().add(kit);
        Location target = f.at(5, 4);
        target.setGround(new Soil());
        String result = new PlaceWeatherStructureAction(kit, target, "East")
                .execute(actor, f.map);
        assertTrue(result.contains("places"));
        assertInstanceOf(DrainageStructure.class, target.getGround());
        assertFalse(actor.getInventory().getItems().contains(kit));
    }

    @Test
    void placementRejectsDistantTileWithoutLosingKit() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Actor actor = f.actorAt(4, 4);
        WeatherStructureKit kit = new WeatherStructureKit(drainage());
        actor.getInventory().add(kit);
        Location target = f.at(8, 8);
        String result = new PlaceWeatherStructureAction(kit, target, "Far Away")
                .execute(actor, f.map);
        assertTrue(result.contains("Cannot build there"));
        assertInstanceOf(Dirt.class, target.getGround());
        assertTrue(actor.getInventory().getItems().contains(kit));
    }

    @Test
    void placementRejectsOccupiedTileWithoutLosingKit() throws Exception {
        WeatherTestFixture f = new WeatherTestFixture();
        Actor actor = f.actorAt(4, 4);
        f.at(5, 4).setGround(new Soil());
        f.actorAt(5, 4);
        WeatherStructureKit kit = new WeatherStructureKit(drainage());
        actor.getInventory().add(kit);
        String result = new PlaceWeatherStructureAction(kit, f.at(5, 4), "East")
                .execute(actor, f.map);
        assertTrue(result.contains("Cannot build there"));
        assertTrue(actor.getInventory().getItems().contains(kit));
    }



    @Test
    void naturalEvaporationTransitionsThroughMudThenRestoresOriginal() throws Exception {
        WeatherTestFixture fixture = new WeatherTestFixture();
        Location tile = fixture.at(3, 3);
        Ground original = new Dirt();
        tile.setGround(new FloodedGround(original));

        // Drying speed is temperature-dependent; check the transition, not a fixed turn count.
        for (int turn = 0; turn < 40 && tile.getGround() instanceof FloodedGround; turn++) {
            tile.getGround().tick(tile);
        }
        MudGround mud = assertInstanceOf(MudGround.class, tile.getGround(),
                "Natural evaporation must create Mud before restoring the original ground");
        assertSame(original, mud.getOriginalGround(),
                "The Mud stage must retain the exact original Ground instance");

        for (int turn = 0; turn < 40 && tile.getGround() instanceof MudGround; turn++) {
            tile.getGround().tick(tile);
        }
        assertSame(original, tile.getGround(),
                "Mud must eventually dry to the original Ground instance");
    }

    @Test
    void drainageRestoresOriginalImmediatelyWithoutMud() throws Exception {
        WeatherTestFixture fixture = new WeatherTestFixture();
        Location tile = fixture.at(3, 3);
        Ground original = new Dirt();
        FloodedGround flooded = new FloodedGround(original);
        tile.setGround(flooded);

        assertTrue(flooded.drain(tile));
        assertSame(original, tile.getGround(),
                "Drainage must bypass Mud and restore the original Ground immediately");
        assertFalse(flooded.drain(tile),
                "A stale FloodedGround reference must not drain the same tile twice");
    }


    /** Isolated local map and actors; never accesses external weather APIs. */
    private static final class WeatherTestFixture {
    final GameMap map;
    final World world;

    WeatherTestFixture() throws GameEngineException {
        this(9, 9);
    }

    WeatherTestFixture(int width, int height) throws GameEngineException {
        DefaultGroundCreator factory = new DefaultGroundCreator();
        factory.registerGround('.', Dirt::new);
        map = new GameMap("REQ5 fixture", factory, '.', width, height);
        world = new World(new Display()) {};
        world.addGameMap(map);
    }

    Location at(int x, int y) {
        return map.at(x, y);
    }

    Actor actorAt(int x, int y) throws GameEngineException {
        Actor actor = new Actor("Test actor", '@', 100) {
            @Override
            public Action playTurn(ActionList actions, Action lastAction,
                                   GameMap gameMap, Display display) {
                return new DoNothingAction();
            }
        };
        map.addActor(actor, at(x, y));
        return actor;
    }

    }

}
