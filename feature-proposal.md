# Assignment 3- Creative Mode Feature Proposal

## REQ 3-The Drunken Tavern

The Farmer can visit the Drunken Tavern and accept one of three fantasy quest, each transforming a quest arena into different challenge. Completing a contract earns coins and experience points, while failing or abandoning a quest can introduce penalties such as mob spawning and other related stuff. Farmer who cannot afford the quest can instead challenge the bartender to a drinking contest, the downside is that he may receive a difficult quest if he wins.

## The Mechanics
### Tavern Quest System
The Farmer can travel between the inherited farm and the Drunken Tavern using connected doorways. The bartender provides an interactives dialogue system where the Farmer can ask about the quests, choose a quest or participate in a drinking content.

Three Quest are available:

| Quest         | Entry fee     | Coin reward |XP reward|Time limit|
| ------------- |:-------------:|:-----------:|:-------:|:--------:|
| The Cursed Forest      | 120     | 280             | 100         | 45 turns         |
| The Lost Survivors      | 200     | 450            | 160        | 55 turns          |
| The Ancient Relic      | 350     | 800            |  260       | 65 turns         |

Accepting a quest deducts the required coins and transport farmer to the Quest Arena. Each quest uses the same arena but generate different terrain, objectives, items and enemies.

The farmer must activate all required objectives before the contracts time limit expires.

Completing a quest reward the farmer with coins, experience and triggers chnages on the inherited farm. Abandoning a quest or exceeding the time limit will trigger consequences, this inslcude but not limited to hostile creature spawning and terrain corruption

The arena is reset between quest so it can be reused for different quest

### Drinking Contest
When a Farmer cannot afford at least one contract, the bartender provides the option to enter a drinking content

The content consists of three rounds, with two choices available for each round

| Sip  | Chug |
| ------------- |:-------------:|
| Safely drinks one cup      | Drinks two cups with a 20% chance of blacking out     |

The bartender drinks a randomly one or two drinks each round.

The farmer must finish with more cups than the bartender before blacking out. Winning frants entry to a randomly selected quest for free. Each quest has a set probability of being chosen with the hardest quest having a 15% rate of being chosen.

Losing or tying results in a 4 points stamina deduction and no quest is handed to you. Farmer is able to challenge the bartender again after 5 turn

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

| Class  | Complex effect |
| ------------- |:-------------:|
| ForestConsequence     | Success: Restore soil and generate a Strawberry. Failure: Converts farm terrain into Corrupted Dirt and spawns a Slime     |
| RescueConsequence      | Success: Restore Soil, Spawns a Chicken and creates an Egg. Failure:Creates Rock terrain, drops a Stone item and spawns a Slime     |
| RelicConsequence      | Success: Restores Soil and places Diamond and Copper items. Failure: Creates Geode and Corrupted Dirt terrain and spawns a Cave Ghost     |

## Supporting Class
**Quest management:**
* QuestManager - Manages Quest acceptance, objective tracking, time limit, completion, failure and player transportation

* QuestDefinition - Stores quest name, fee, reward, time limit, rules, scenario and consequence

* QuestWorldContext - Provides access to farm terrain, item placement and creature spawning

* QuestOutcomesService - Handle quest reward and applies the correct failure and success

* QuestSetupContext - controll acces to arena preparation

* ArenaQuest - Manges the resuable arena, including placement and reset

**Quest interaction**
* AcceptContractAction - Allow farmer to accept QUest

* QuestObjectiveGround - represent any interactive objectives inside the arena

* QuestClockGround - Deals the the quest timer

* QuestJournal - Provides ability to farmer to abandon the quest

* QuestAbandonAction - Handles abandoning a contract

**Tavern and drinking contest**

* ContractBartender - Provides the Farmer with available tavern interaction

* BartenderDialogue - Manages dialoge stages and conversation choices

* DrinkingContest - Manages the drinking rounds, scores and the chances of getting blackout, its cooldown and random Quest selection

* DrinkRoundAction - Processes individual drinking rounds

* StartDrinkingContestAction - Starts an eligible drinking contest.

* TavernDoor, QuestExitDoor, and TavernTravelAction - Support transportation between game locations.

* TavernFloor and TavernWall - Represent Tavern terrain

* TavernAbility - Controls eligibility to use Tavern interactions.

* ExperienceTracker - Maintains the Farmer's accumulated and available experience points.
# This is a Heading h1
## This is a Heading h2
###### This is a Heading h6

## Emphasis

*This text will be italic*  
_This will also be italic_

**This text will be bold**  
__This will also be bold__

_You **can** combine them_

## Lists

### Unordered

* Item 1
* Item 2
* Item 2a
* Item 2b
    * Item 3a
    * Item 3b

### Ordered

1. Item 1
2. Item 2
3. Item 3
    1. Item 3a
    2. Item 3b

## Images

![This is an alt text.](/image/Markdown-mark.svg "This is a sample image.")

## Links

You may be using [Markdown Live Preview](https://markdownlivepreview.com/).

## Blockquotes

> Markdown is a lightweight markup language with plain-text-formatting syntax, created in 2004 by John Gruber with Aaron Swartz.
>
>> Markdown is often used to format readme files, for writing messages in online discussion forums, and to create rich text using a plain text editor.

## Tables

| Left columns  | Right columns |
| ------------- |:-------------:|
| left foo      | right foo     |
| left bar      | right bar     |
| left baz      | right baz     |

## Blocks of code

```
let message = 'Hello world';
alert(message);
```

## Mermaid diagrams
```mermaid
graph TD
  A[Start] --> B{Decision}
  B -->|Yes| C[Finish]
  B -->|No| D[Alternate]
```

## Inline code

This web site is using `markedjs/marked`.



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

* **Tidal Cauldron:** When its brew finishes, it floods all ground within radius 2 into Puddles, a terrain change that also pulls ground items into the cauldron's tile.
* **Ward Cauldron:** On every turn of the brew, it damages every hostile creature within radius 3, an area-of-effect over time.
* **Quarry Cauldron:** On every turn of the brew, it has a chance to spawn an ore node (Copper Node or Geode) on a random empty tile within radius 5, dynamic spawning over time.

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
| 4   | `TidalCauldron`    | `BrewingStation`     | New    |
| 5   | `WardCauldron`     | `BrewingStation`     | New    |
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

**Tornado**: Strong winds scatter items across adjacent locations, intorduces fallen tree obstalce and distrupt nearby actors.

### Environmental Response

**Drainage response:** Floodwater is redirected into drainage channels,floodied terrain is restored to Soil and collected water becomes usable

**Wildlife Response:** Chickens and Cows attempt to relocate to safer tiles during dangerous weather. The process can alter nearby terrain and generate animal products at their new locations

**Resources Response:** Weather-damaged terrain exposes additonal resources, converting suitablr ground into mining nodes and placing collectible materials nearby. 