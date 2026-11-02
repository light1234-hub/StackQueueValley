# Assignment 3- Creative Mode Feature Proposal

## REQ 3- QuestBoard Challenge

The Farmer can interact with a Quest Board on the farm to accept one of three fantasy contracts, each transforming a reusable Quest Arena into a different challenge. Completing a contract grants coins and experience points and changes the farm, while failing or abandoning one triggers persistent consequences. A Farmer who cannot afford a contract can attempt a three-round dice challenge for a chance to enter a quest without paying its entry fee.

## The Mechanics
### Tavern Quest System
The Farmer interacts with the Quest Board on the farm to view available contracts or attempt a dice challenge when a contract is unaffordable

Three Quest are available:

| Quest         | Entry fee     | Coin reward |XP reward|Time limit|
| ------------- |:-------------:|:-----------:|:-------:|:--------:|
| The Cursed Forest      | 120     | 280             | 100         | 45 turns         |
| The Lost Survivors      | 200     | 450            | 160        | 55 turns          |
| The Ancient Relic      | 350     | 800            |  260       | 65 turns         |

* Accepting a paid contract deducts its entry fee and transport the Farmer to the shared Quest Arena
* Each QuestScenario configures the arena with its own terrain, objectives, items and enemies
* The Farmer must complete the required objectives before the contract deadline. The Quest Journal allows the Farmer to abandon a quest.
* Success awards coins and XP and applies that quest's success consequence to the farm. Failure, abandonment or timeout applies its failure consequence instead.
* The arena resets between quests so different scenarios can reuse it.

### Dice Challenge
A Farmer who lacks enough coins for a contract may attempt a dice challenge to obtain entry without paying.

The challenge has three rounds; each round offers the following choices:

| Choice  |                        Effect                        |
| ------------- |:----------------------------------------------------:|
| Safe roll     |  Roll a six-sided die (1–6); all results add to the score   |
| Risky roll   | Roll a ten-sided die (1–10); rolling 1 or 2 immediately loses the challenge.|
 | Forfeit    | 	End the challenge and take the normal failure penalty.|

Winning provides the opportunity to enter the challenged quest for free. Losing or forfeiting applies a four-stamina penalty; the existing challenge cooldown limits immediate retries.

## Architecture
Here we introduce two new interface and 6 brand new concrete classes with 3 concrete implementation for each interface

**Abstraction 1: QuestScenario**

Type: New interface

Responsibility: Defines how each cotract prepares the Quest Arena by placing specific terrain,objectives, items and enemies

**Concrete implementation:**

| Class  | Complex effect |
| ------------- |:-------------:|
| CursedForestScenario      | Transform multiple arena tiles into corrupted ground, creates three ritual root objectives and spawn hostile slime     |
| RescueMissionScenario      | Creates a blocked rescue route using Rock terrain, places two survivor-cage objectives and supplies, and spawns a guarding Bat.    |
| AncientRelicScenario      | Creates hazardous ruins with Geodes, Copper Nodes, Rock and corrupted terrain, places a Copper item, spawns a Cave Ghost, and creates three ordered objectives.     |

**Abstraction 2: QuestConsequence**
Type: New interface

Responsibility: Defines how a completed or failed contract changes the inherited farm.

| Class  |                                                                                                             Complex effect                                                                                                              |
| ------------- |:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| ForestConsequence     |                                                         Success: Creates a magical grove that gradually purifies corrupted terrain within a radius and allows new vegetation to grow on restored land. Failure: Creates a corruption source that expands across adjacent Soil over time, infects crops and applies a harmful status to actors entering contaminated areas.                                                        |
| RescueConsequence      |                                                     Success: Spawns rescued survivors with AI behaviour. They collect dropped resources, navigate back to a supply camp and gradually repair damaged farmland. Failure:Introduces hostile scavengers that patrol the farm, steal dropped resources and construct traps that restrict movement.                                                     |
| RelicConsequence      |   Success: Generates connected magical crystal veins that trigger a chain reaction when mined, transforming multiple terrain tiles and producing resources. Failure: Creates unstable terrain that collapses after a countdown, affecting nearby actors, scattering items and generating obstacles   |

## Supporting Class
**Quest management:**
* QuestManager - coordinates contract acceptance, objective progression, deadlines, completion/failure, and return travel.

* QuestDefinition - Stores quest name, fee, reward, time limit, rules, scenario and consequence

* QuestWorldContext - Provides access to farm terrain, item placement and creature spawning

* QuestOutcomesService - Handle quest reward and applies the correct failure and success

* QuestSetupContext - controll acces to arena preparation

* ArenaQuest - Manges the resuable arena, including placement and reset

**Quest interaction**
* QuestBoardGround - provides the on-map interaction point for contracts and dice challenges.

* AcceptContractAction - Allow farmer to accept Quest

* QuestObjectiveGround - represent any interactive objectives inside the arena

* QuestClockGround - tracks quest deadlines.

* QuestJournal and QuestAbandonAction- Provides ability to farmer to abandon the quest

**Dice challenge**
* DiceWager - manages the challenge's state, rolls and outcomes.
* DiceChoice - defines Safe, Risky and Forfeit choices.
* StartDiceWagerAction - begins an eligible challenge.
* PlayDiceRoundAction - resolves a selected dice choice.
* ExperienceTracker - Maintains the Farmer's accumulated and available experience points.


## REQ 4 - Alchemy Valley

## The Pitch

Alchemy Valley is a new map, reached through a paid Wishing Well, where the farmer brews potions in three cauldrons. Brewing is not instant: the farmer loads ingredients (and must have enough XP), and the cauldron brews over several turns, changing the world around it while it works. When the potion is collected and drunk, it triggers a powerful effect such as knockback, damage reflection, or terrain-breaking item magnetism.

## The Mechanics

### Travel and the Wizard

A `WishingWell` on the farm map lets the farmer perform a `WishAction`. It costs 100 coins and teleports the farmer to Alchemy Valley. A second well in Alchemy Valley returns the farmer to the farm.

A Wizard NPC in Alchemy Valley tells the farmer each potion's ingredients, XP requirement and brewing time, and how much XP the farmer is still missing.

### Multi-turn Brewing

1. The farmer uses a `LoadAction` on a cauldron while holding the correct ingredients and enough XP. The ingredients are consumed and the brew timer starts.
2. Each turn the cauldron ticks down its timer and applies its own in-brew effect.
3. When the timer ends, the cauldron is marked READY. The farmer uses a `CollectAction` to take the potion. A cauldron cannot be loaded again until its potion has been collected.

XP is a threshold that is checked and not spent. It is earned through the quest and XP feature built by a teammate for REQ3. This feature only reads the XP value.

### Recipes

Three potions are available:

| Potion              | Ingredients                      | XP Required | Brew Time |
|---------------------|----------------------------------|:-----------:|:---------:|
| Gale Draught        | 3 Strawberry, 1 Tomato, 1 Copper |     50      |  3 turns  |
| Aegis Elixir        | 3 Stone, 2 Strawberry, 1 Diamond |     100     |  4 turns  |
| Prospector's Elixir | 2 Copper, 2 Tomato, 1 Stone      |     150     |  5 turns  |

### Potion Effects

Drinking reuses the existing `Consumable` and `ConsumeAction`.

* **Gale Draught:** AoE, radius 3. Every actor in range is pushed up to 3 tiles directly away from the farmer. Anything blocked by an obstacle stops early. Hostile creatures hit this way are stunned for 2 turns.
* **Aegis Elixir:** Applies a reflect Status for 10 turns. While active, the farmer takes no damage, and every attacker takes the damage that was blocked.
* **Prospector's Elixir:** AoE, radius 4. Every item on the ground in range is pulled into the farmer's inventory, and every Rock in range is shattered and replaced with Stone items on the ground, which changes the terrain.

### Cauldron Effects While Brewing

Each cauldron is linked to one potion and changes the world every turn of the brew.

* **Orchard Cauldron (brews Gale Draught, 3 turns):** Each turn it plants one `ArcaneSapling` on a random empty tile on the map. A sapling grows over 3 turns into an `ArcaneTree`, even after the potion is collected. A tree cannot be walked through and drops a random Strawberry or Tomato on its tile every 2 turns. A hostile creature that steps onto a sapling tramples it and it is removed.
* **Taming Cauldron (brews Aegis Elixir, 4 turns):** Each turn it places one `Lure` item on a random empty tile on the map. Every hostile creature drops its current behaviour and walks toward a random lure. When a creature reaches a lure, the lure is used up and the creature permanently gets a `TAMED` status: it stops attacking the farmer and instead attacks any untamed hostile creature it meets. Unused lures disappear when the potion is collected.
* **Quarry Cauldron (brews Prospector's Elixir, 5 turns):** When the brew starts it spawns a `StoneGolem` actor. Each turn the golem walks to a random Rock on the map and shatters it, removing the Rock and adding Stone items to the cauldron's output. The golem crumbles when the potion is collected.

## The Architecture

### New Abstractions

| Abstraction      | Type           | Extends  |
|------------------|----------------|----------|
| `Potion`         | Abstract class | `Item`   |
| `BrewingStation` | Abstract class | `Ground` |

`Potion` holds the ingredient list, XP requirement, and a `drink` method that each potion implements. `BrewingStation` holds the brew timer, the READY state, the load and collect logic, and an abstract hook for the in-brew effect that each cauldron implements.

### Concrete Classes

| #   | Class              | Implements / Extends | Status |
|-----|--------------------|----------------------|--------|
| 1   | `GaleDraught`      | `Potion`             | New    |
| 2   | `AegisElixir`      | `Potion`             | New    |
| 3   | `ProspectorElixir` | `Potion`             | New    |
| 4   | `OrchardCauldron`    | `BrewingStation`     | New    |
| 5   | `TamingCauldron`     | `BrewingStation`     | New    |
| 6   | `QuarryCauldron`   | `BrewingStation`     | New    |

All six concrete classes are entirely new. No classes from Assignments 1 to 3 are retrofitted.

### Supporting Classes

These are not counted towards the six.

* `WishingWell` (extends `Ground`) and `WishAction`: the travel mechanism.
* `Wizard` (extends `Actor`): the recipe guide NPC.
* `LoadAction` and `CollectAction`: the farmer's actions at a cauldron.
* `AlchemyValleyWorld` (map): the new map containing the three cauldrons, the wizard, and the return well.

### Existing Classes Used (Unmodified)

`Item`, `Ground`, `Actor`, `Status`, `Consumable`, `ConsumeAction`, `Puddle`, `Rock`, `CopperNode`, `Geode`, `StoneItem`, `CopperItem`, `DiamondItem`, `StrawberryItem`, `TomatoItem`, `FinanceStatistics`.

## REQ 5: Weather System

We will introduce a synamic weather system that uses real world weather information to nfluce Farmer's environment. Depending on multitude of factors such as wind condition or even precipitation retrived from external API, the farm can experience flooding, heatwave or even strong winds that would modify the farms terrain, affect nearby actors and redistribute resources. Different Environmental responses allow the farm to adpat to changing conditions, crreating new gameplay challenges and interactions.

## The Mechanics

### Weather System
The game retrieves weather information using the Open-Meteo Forecast API. The API then generate a request that uses Farmer's current in game coordinates, mapped to geographical coordinates. The response provides temperature, precipitation, wind speed, and weather conditions. Weather updates occur periodically rather than making an API request every game turn. Different weather conditions activate different environment effects.

### Weather Events
**Rainstorm**: Heavy rainful causes selected soil to turn into puddle temporarily and applies a soak status to actors within the affected area

**Heatwaves**: High temperature turn selected soil tiles into dry ground, create a temporary fire hazard and apply burning effects to actors standing on affected tiles.

**Tornado**: Strong winds scatter items across adjacent locations, introduces fallen tree obstacles and disrupt nearby actors.

### Environmental Response

**Drainage response:** Floodwater is redirected into drainage channels,flooded terrain is restored to Soil and collected water becomes usable

**Wildlife Response:** Chickens and Cows attempt to relocate to safer tiles during dangerous weather. The process can alter nearby terrain and generate animal products at their new locations

**Resources Response:** Weather-damaged terrain exposes additional resources, converting suitable ground into mining nodes and placing collectible materials nearby. 

## Architecture
Here we introduce two new interface and 6 brand new concrete classes with 3 concrete implementation for each interface in Req 5

**Abstraction 1: WeatherEvent**

Type: New interface

Responsibility: Defines how a weather condition changes by modifying the terrain of the farm, affecting actors and redistributing resources.

**Concrete implementation:**

| Class         |                                                                               Complex effect                                                                               |
|---------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| RainstormEvent | Converts selected tiles into temporary puddles and applies a soaked status to all actors caught in the storm. Actor can only move 1 step every 3 turns while being soaked. |
| HeatwaveEvent |            Converts selected tiles into dry ground, creates temporary fire hazards and applies the burning effect to all actors standing on the selected tiles.            |
| Tornado       |   Scatters items into nearby adjacent locations, creates fallen-tree obstacles and disrupts nearby actors. Fallen trees block 2 tiles and forces actors to move around.    |

**Abstraction 2: WeatherResponse **
Type: New interface

Responsibility: Defines how the farm, the animals within said farm and its resources respond to weather damages and hazards.

| Class                    |                                                                                                                      Complex effect                                                                                                                      |
|--------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| DrainageResponse         |        Redirects floodwater into drainage channels, changes the temporary puddles back into soil and makes the collected rainwater available to be used by the farmer. DrainageResponse can be crafted by the farmer and placed down on the map.         |
| WildlifeResponse         |          Moves Chicken and Cows into towards safe tiles during any sort of dangerous weather events. The animals will also spawn new animal items at their new items. WildlifeResponse can be crafted by the farmer and placed down on the map.          |
| ResourceRecoveryResponse | Uses weather-damaged terrain to expose and collect additional resources, changing ground into mining ores. ResourceRecoveryResponse can be crafted by the farmer and placed down on the map. It spawns mining ore on the farm on the selected locations. |