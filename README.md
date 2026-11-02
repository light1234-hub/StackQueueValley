# StackQueue Valley

**FIT2099 Assignment (Semester 2, 2026)**

```text
   ,-,--.  ,--.--------.   ,---.       _,.----.  ,--.-.,-.
 ,-.'-  _\/==/,  -   , -\.--.'  \    .' .' -   \/==/- |\  \
/==/_ ,_.'\==\.-.  - ,-./\==\-/\ \  /==/  ,  ,-'|==|_ `/_ /,--.--------.
\==\  \    `--`\==\- \   /==/-|_\ | |==|-   |  .|==| ,   //==/,  -   , -\
 \==\ -\        \==\_ \  \==\,   - \|==|_   `-' \==|-  .| \==\.-.  - ,-./
 _\==\ ,\       |==|- |  /==/ -   ,||==|   _  , |==| _ , \ `--`--------`
/==/\/ _ |      |==|, | /==/-  /\ - \==\.       /==/  '\  |
\==\ - , /      /==/ -/ \==\ _.\=\.-'`-.`.___.-'\==\ /\=\.'
 `--`---'       `--`--`  `--`                    `--`
    _,.---._                     ,----.                  ,----.
  ,-.' - ,  `.   .--.-. .-.-. ,-.--` , \ .--.-. .-.-. ,-.--` , \
 /==/ ,    -  \ /==/ -|/=/  ||==|-  _.-`/==/ -|/=/  ||==|-  _.-`
|==| - .=.  ,  ||==| ,||=| -||==|   `.-.|==| ,||=| -||==|   `.-.
|==|  : ;=:  - ||==|- | =/  /==/_ ,    /|==|- | =/  /==/_ ,    /
|==|,  '='  ,  ||==|,  \/ - |==|    .-' |==|,  \/ - |==|    .-'
 \==\ _   -    ;|==|-   ,   /==|_  ,`-._|==|-   ,   /==|_  ,`-._
  '.='.  ,  ; -\/==/ , _  .'/==/ ,     //==/ , _  .'/==/ ,     /
    `--`--'' `--`--`..---'  `--`-----`` `--`..---'  `--`-----``
        ,-.-.  ,---.                              ,----.
 ,--.-./=/ ,/.--.'  \       _.-.      _.-.     ,-.--` , \ ,--.-.  .-,--.
/==/, ||=| -|\==\-/\ \    .-,.'|    .-,.'|    |==|-  _.-`/==/- / /=/_ /
\==\,  \ / ,|/==/-|_\ |  |==|, |   |==|, |    |==|   `.-.\==\, \/=/. /
 \==\ - ' - /\==\,   - \ |==|- |   |==|- |   /==/_ ,    / \==\  \/ -/
  \==\ ,   | /==/ -   ,| |==|, |   |==|, |   |==|    .-'   |==|  ,_/
  |==| -  ,//==/-  /\ - \|==|- `-._|==|- `-._|==|_  ,`-._  \==\-, /
  \==\  _ / \==\ _.\=\.-'/==/ - , ,/==/ - , ,/==/ ,     /  /==/._/
   `--`--'   `--`        `--`-----'`--`-----'`--`-----``   `--`-`
```

## Overview

StackQueue Valley is a Java console game built for the FIT2099 assignment. The player explores a farm and a mine, uses tools and items, manages crops and animals, and encounters hostile creatures. The project includes the FIT2099 engine source under `src/edu/monash/fit2099/engine`.

## Running the game

The repository does include a Maven build file which is called pom.xml. You need to run the pom.xml as a maven file. You can import the project into a Java IDE, mark `src` as a source folder, and run `game.app.Application`. The combined application requires weather coordinates for the Assignment 3 REQ5 weather system; see that section below before launching.

To compile and launch from PowerShell with a JDK installed:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$javaFiles = Get-ChildItem .\src -Recurse -Filter *.java | ForEach-Object FullName
javac -d out $javaFiles
java -cp out game.app.Application
```

## Running the tests

The project uses JUnit 5 and Mockito. Maven downloads the test dependencies and runs the tests through Surefire. Run commands from the project root, in the same folder as `pom.xml`:

```powershell
mvn test
```

This runs all 149 JUnit tests under `src/test/java`. The tests are grouped by requirement:

- **REQ1:** `game.req1tests.PlantCropFactoryUnitTest` (4 tests)
- **REQ2:** `game.req2tests.Req2CookingTest` (21 tests)
- **REQ3:** `game.req3tests.Req3SystemTest` (17 tests)
- **REQ4:** `game.req4tests.AegisAttributionTest` (1 test)
- **REQ4:** `game.req4tests.BrewingStationTest` (8 tests)
- **REQ4:** `game.req4tests.CauldronEffectsTest` (8 tests)
- **REQ4:** `game.req4tests.PotionEffectsTest` (8 tests)
- **REQ4:** `game.req4tests.PotionRecipeTest` (4 tests)
- **REQ4:** `game.req4tests.WishingWellAndWizardTest` (7 tests)
- **REQ5:** `game.req5tests.Req5WeatherTest` (35 tests)

`Req5WeatherTest` uses a fake weather provider, so it does not make live API requests. Its simulated network-failure case verifies the coordinator's error handling and captures the expected log output.

To run one class from PowerShell, use its class name:

```powershell
mvn -Dtest=PlantCropFactoryUnitTest test
mvn -Dtest=Req2CookingTest test
mvn -Dtest=Req3SystemTest test
mvn -Dtest=AegisAttributionTest test
mvn -Dtest=BrewingStationTest test
mvn -Dtest=CauldronEffectsTest test
mvn -Dtest=PotionEffectsTest test
mvn -Dtest=PotionRecipeTest test
mvn -Dtest=WishingWellAndWizardTest test
mvn -Dtest=Req5WeatherTest test
```

In IntelliJ, open the project as a Maven project by opening `pom.xml` and reloading Maven. If needed, right-click `src/test/java` and select **Mark Directory as → Test Sources Root**. Use the green run icon beside a test class or right-click `src/test/java` and choose **Run 'All Tests'** to run the entire suite.

## Gameplay overview

- Explore the farm and the mines, which are linked by ladders and teleportation circles.
- Grow and harvest crops, interact with farm animals, and use tools and collected items.
- Mine resources and interact with terrain and characters in the world.
- Select actions through the game's menu as your Farmer takes turns.

## Assignment 2 REQ5: Zombie states

The Zombie uses interchangeable states for wandering, attacking, fleeing, and healing. Its attacking behavior searches for the nearest reachable `ZombieTarget`, attacks when a target is adjacent, and wanders when it cannot find a reachable target.

### AttackingState

When the Zombie enters `AttackingState`, it receives a temporary 10% attack damage increase and 15% hit chance increase for that turn. It also leaves a temporary poison trap on its original tile and attempts to lunge one safe tile toward the nearest reachable target. The lunge is skipped if no target is reachable or the first tile is unsafe. The trap remains in either case. A `PoisonTrap` lasts up to 10 turns and poisons the first `ZombieTarget` that occupies its tile; the resulting `PoisonEffect` lasts three turns.

Key classes and interfaces:

- `game.actor.Zombie`
- `game.actor.ZombieTarget`
- `game.state.CreatureState`
- `game.state.AttackingState`
- `game.state.FleeingState`
- `game.state.HealingState`
- `game.state.WanderingState`
- `game.effect.PoisonTrap`
- `game.effect.PoisonEffect`

### WanderingState

The Zombie can switch between wandering, attacking, fleeing, and healing. This section describes Kavin's contribution: the Zombie actor, `WanderingState`, the shared `ZombieSensor` helper, and the dirt-corruption feature. The remaining states are covered by the team in the REQ5 section.

`WanderingState` is the Zombie's default state. Each turn, it uses `ZombieSensor` to check the Zombie's health and whether a target or food is within `SCAN_RANGE`. It then chooses a state:

1. If health is low and food is nearby, the Zombie enters `HealingState`.
2. If a target is nearby and the Zombie's health is too low to fight, it enters `FleeingState`.
3. If a target is nearby and the Zombie has enough health, it enters `AttackingState`.
4. Otherwise, it wanders using `WanderBehaviour`. It does nothing if it cannot move.

`ZombieSensor` centralizes chxecks for health, nearby targets, and nearby food, along with the health thresholds. This lets each state use the same rules rather than repeating them.

#### Corrupting dirt

When the Zombie enters `WanderingState`, its `onEnter` action corrupts the ground on every adjacent tile. `WanderingState` checks for the `CAN_BE_CORRUPTED` terrain ability rather than checking concrete ground types, then calls the `corrupt` method on ground that implements `Corruptible`.

> “Each ground decides for itself what it will become.”

Only Dirt is corruptible for now; it replaces itself with `CorruptedDirt`. Ladders, other terrain, tilled soil, puddles, and crops are left alone. To add another corruptible ground, implement `Corruptible` and set `CAN_BE_CORRUPTED` to true. The Zombie code stays the same.

Key classes and interfaces:

- `game.actor.Zombie`
- `game.state.WanderingState`
- `game.state.ZombieSensor`
- `game.blueprint.Corruptible`
- `game.ability.TerrainAbility` (`CAN_BE_CORRUPTED`)
- `game.terrain.Dirt`
- `game.terrain.CorruptedDirt`

The Zombie is placed on the map in `StackQueueValleyWorld`.

### HealingState

The Zombie's healing behavior uses `SeekFoodBehaviour` to search for the nearest reachable `Consumable` item using breadth-first search (BFS). If food is on its current tile, it consumes the item to recover health. Otherwise, it moves toward the nearest reachable food. If no food is found, the Zombie remains idle. It transitions to `AttackingState` when its health reaches at least 70% and a target is detected within three tiles, or to `WanderingState` when it has recovered and neither nearby targets nor reachable food are detected.

When the Zombie enters `HealingState`, it performs a one-time food-gathering effect using BFS to search reachable locations within two tiles. Consumable items found are moved onto the Zombie's current tile. If no food is collected, two `ParsnipItem` objects are generated there. This entry effect is separate from its ordinary food-seeking behavior.

Key classes and interfaces:

- `game.state.HealingState`
- `game.state.CreatureState`
- `game.state.ZombieSensor`
- `game.behaviour.SeekFoodBehaviour`
- `game.farmeraction.EatGroundFoodAction`
- `game.blueprint.Consumable`
- `game.item.ParsnipItem`

### FleeingState

The Zombie's fleeing behavior uses `FleeBehaviour` to detect the nearest `ZombieTarget` within five tiles and move away from it. It attempts to escape by moving two tiles in the opposite direction of the threat. If a two-tile move is not possible, it attempts a one-tile move or selects another accessible tile that increases its distance from the target. If no suitable escape location is available, it remains idle. It transitions to `HealingState` when its health falls below 50%, it is not poisoned, and either no target is immediately adjacent or reachable food is detected within two tiles. It transitions to `WanderingState` when no target is detected within three tiles. Otherwise, it remains in `FleeingState`.

When the Zombie enters `FleeingState`, its `onEnter()` method performs a one-time knockback effect using BFS to detect nearby `ZombieTarget` actors within two tiles. Each detected target is pushed two tiles away from the Zombie if the destination is accessible. If the two-tile destination is unavailable, the Zombie attempts to push the target one tile instead. If neither destination is accessible, the target remains in its original location. This entry effect is separate from its ordinary escape behavior.

Key classes and interfaces:

- `game.state.FleeingState`
- `game.state.CreatureState`
- `game.state.ZombieSensor`
- `game.behaviour.FleeBehaviour`
- `game.zombieaction.TwoStepMoveAction`
- `game.actor.ZombieTarget`

## Assignment 3 REQ5: Weather system

### Weather data and event selection

`WeatherCoordinator` refreshes weather every five world turns. It maps the farm location to latitude and longitude, retrieves a snapshot through `OpenMeteoProvider`, generates weather values for each farm tile, updates `WeatherContext`, and asks `WeatherEvaluator` to select an event. The default rules require at least five qualifying tiles: wind of 70 km/h or higher triggers a Tornado, temperature of 35 °C or higher triggers a Heatwave, and rainfall of 1.0 mm/h or higher triggers a Rainstorm. If more than one rule qualifies, the first rule in the configured order is selected: Tornado, Heatwave, then Rainstorm. The selected event affects tiles within a radius of two from the current farm location.

The application requires `WEATHER_LATITUDE` and `WEATHER_LONGITUDE` environment variables. In PowerShell, set them before launching; this example uses Kuala Lumpur coordinates:

```powershell
$env:WEATHER_LATITUDE = '3.1390'
$env:WEATHER_LONGITUDE = '101.6869'
```

In IntelliJ, add both variables under **Run → Edit Configurations → Environment variables** for the Application configuration. Use coordinates for any real-world location you want the weather system to represent.

### Weather events and temporary terrain

- **Rainstorm:** Floods affected terrain unless it is already drainable or marked flood-resistant. Actors on flooded tiles receive `SoakedStatus` for three turns. `FloodedGround` naturally dries into `MudGround`, which later restores the original terrain.
- **Heatwave:** Replaces puddle-capable terrain with `ScorchedGround`. Scorched terrain has a fire effect and can be watered to restore the ground it replaced.
- **Tornado:** Scatters items to adjacent tiles, attempts to move actors to adjacent safe tiles, and creates `FallenTreeGround` on suitable unoccupied, unprotected terrain. Fallen trees expire after five turns or can be recovered for either five Copper or ten Stone.

The terrain types retain the ground they replaced, so each event effect can restore or transition terrain appropriately. Flooded terrain uses temperature-dependent drying; natural flooding passes through mud, while drainage can bypass that stage.

### Weather response structures and crafting

`WeatherCraftingProvider` offers three structure recipes: Drainage Structure (two Stone and one Copper), Wildlife Shelter (three Stone and one Copper), and Resource Recovery Structure (one Stone and two Copper). Crafting creates a kit. `PlaceWeatherStructureAction` places it on a valid adjacent tile, installs the structure, and then calls `onPlaced()`.

`WeatherResponseStructure` shares protection, entry, and activation rules. Shelter and Resource Recovery structures run their responses automatically each world turn. Shelter moves at most one endangered animal to safety per turn; the animal's `WeatherShelterable.onRelocated()` behavior can produce an Egg or Milk. Resource Recovery processes at most one eligible terrain tile per turn.

Drainage Structure has a different activation mode: it runs once immediately after placement. `DrainageResponse` scans all tiles within Manhattan distance five, excluding the structure's own tile, and attempts to drain every ground implementing `Drainable`. Flooded tiles are restored directly to their original ground, bypassing the natural Mud stage. It does not run again on later world turns; later flooding requires another Drainage Structure.

Key weather classes and interfaces:

- `game.weather.WeatherCoordinator`, `WeatherEvaluator`, `WeatherContext`
- `game.weather.WeatherEvent`, `RainstormEvent`, `HeatwaveEvent`, `TornadoEvent`
- `game.weather.WeatherResponseStructure`, `DrainageStructure`, `Shelter`, `ResourceRecoveryStructure`
- `game.weather.WeatherCraftingProvider`, `WeatherStructurePlan`, `PlaceWeatherStructureAction`
- `game.weatherblueprint.Drainable`, `WeatherRecoverable`, `WeatherHazard`, `WeatherShelterable`
- `game.weatherterrain.FloodedGround`, `MudGround`, `ScorchedGround`, `FallenTreeGround`

## Source layout

| Path | Contents |
| --- | --- |
| `src/game/actor` | Farmer, Zombie, animals, and other actors |
| `src/game/blueprint` | Shared capabilities and interfaces |
| `src/game/behaviour` | Reusable creature behaviors |
| `src/game/effect` | Coatings and temporary effects |
| `src/game/farmeraction` | Actions available to the Farmer |
| `src/game/map` | World and map setup |
| `src/game/state` | Zombie state behavior and sensing |
| `src/game/terrain` | Ground types and crops |
| `src/edu/monash/fit2099/engine` | FIT2099 game engine source |

## Contribution log

[View the contribution log](https://docs.google.com/spreadsheets/d/1VugVD21tl_u1xxZpuDhpY3c8Dqlafoqd6AH90nrcc5E/edit?usp=sharing)

