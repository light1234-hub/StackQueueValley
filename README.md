# FIT2099 Assignment (Semester 2, 2026)

```
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
# StackQueue Valley

StackQueue Valley is a Java console game built for the FIT2099 assignment. The player explores a farm and a mine, uses tools and items, manages crops and animals, and encounters hostile creatures. The project includes the FIT2099 engine source under `src/edu/monash/fit2099/engine`.

## Running the game

The repository does not include a Maven or Gradle build file. You can import the project into a Java IDE, mark `src` as a source folder, and run `game.app.Application`.

To compile and launch from PowerShell with a JDK installed:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$javaFiles = Get-ChildItem .\src -Recurse -Filter *.java | ForEach-Object FullName
javac -d out $javaFiles
java -cp out game.app.Application
```

## Gameplay overview

- Explore the farm and the mines, which are linked by ladders and teleportation circles.
- Grow and harvest crops, interact with farm animals, and use tools and collected items.
- Mine resources and interact with terrain and characters in the world.
- Select actions through the game's menu as your Farmer takes turns.

## REQ5 Zombie states

The Zombie uses interchangeable states for wandering, attacking, fleeing, and healing. Its attacking behavior searches for the nearest reachable `ZombieTarget`, attacks when a target is adjacent, and wanders when it cannot find a reachable target.


## REQ5 Zombie AttackingState
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

## REQ5 Zombie: Kavin Pusparajans part
The Zombie can switch between states such, as wandering, attacking, fleeing and healing. This part shows what Kavin wrote: the Zombie character, the WanderingState, the shared ZombieSensor helper and the dirt corruption feature. The remaining states are handled by the team (see the REQ5 section).
WanderingState
WanderingState is the default state for the Zombie. Every turn it checks with ZombieSensor to find out the Zombies health and if there is a target or food within SCAN_RANGE tiles then it decides on the state:
1.If the health is low and food is close, the Zombie goes to HealingState.
2.If there is a target and the health is too low to fight the Zombie goes to FleeingState.
3.If there is a target and the health is high enough the Zombie goes to AttackingState.
4.If none of these conditions are met the Zombie continues to wander using WanderBehaviour. It does nothing if it cannot move.
ZombieSensor contains the logic, for sensing health level nearby targets and nearby food. ZombieSensor also holds the health thresholds. Every state uses the same rules instead of repeating them.

## Corrupting Dirt
When the Zombie enters WanderingState the onEnter action corrupts the ground on every tile next to it. WanderingState does not check what type of ground it is. It checks for the CAN_BE_CORRUPTED terrain ability. Then calls the corrupt function on the Corruptible ground.

“Each ground decides for itself what it will become. - Only Dirt is corruptible for now, it replaces itself with CorruptedDirt. Ladders, other terrain, tilled soil, puddles, crops are left alone. To add a new corruptible ground, implement Corruptible and set CAN_BE_CORRUPTED to true. The Zombie code stays the same.

Key classes and interfaces
game.actor.Zombie
game.state.WanderingState
game.state.ZombieSensor
game.blueprint.Corruptible
game.ability.TerrainAbility (CAN_BE_CORRUPTED)
game.terrain.Dirt
game.terrain.CorruptedDirt
The Zombie is placed on the map in StackQueueValleyWorld.

## REQ5- HealingState
The Zombie's healing behaviour uses SeekFoodBehaviour to search for the nearest reachable Consumable item using Breadth-First Search (BFS). If food is on its current tile, it consumes the item to recover health. Otherwise, it moves towards the nearest reachable food. If no food is found, the Zombie remains idle. The Zombie transitions to AttackingState when its health reaches at least 70% and a target is detected within three tiles, or to WanderingState when it has recovered and neither nearby targets nor reachable food are detected.
When the Zombie enters HealingState, it performs a one-time food-gathering effect using BFS to search reachable locations within two tiles. Any consumable items found are moved onto the Zombie's current tile. If no food is collected, two ParsnipItem objects are generated on its location. This effect occurs immediately upon entering HealingState, separately from its ordinary food-seeking behaviour.
Key classes and interfaces:
game.state.HealingState
game.state.CreatureState
game.state.ZombieSensor
game.behavior.SeekFoodBehaviour
game.farmeraction.EatGroundFoodAction
game.blueprint.Consumable
game.ietem.ParsnipItem

## REQ 5 - FleeingState
The Zombie's fleeing behaviour uses FleeBehaviour to detect the nearest ZombieTarget within five tiles and move away from it. The Zombie attempts to escape by moving two tiles in the opposite direction of the threat. If a two-tile movement is not possible, it attempts a one-tile movement or selects another accessible tile that increases its distance from the target. If no suitable escape location is available, the Zombie remains idle. The Zombie transitions to HealingState when its health falls below 50%, it is not poisoned, and either no target is immediately adjacent or reachable food is detected within two tiles. It transitions to WanderingState when no target is detected within three tiles. Otherwise, it remains in FleeingState.
When the Zombie enters FleeingState, its onEnter() method performs a one-time knockback effect using Breadth-First Search (BFS) to detect nearby ZombieTarget actors within two tiles. Each detected target is pushed two tiles away from the Zombie if the destination is accessible. If the two-tile destination is unavailable, the Zombie attempts to push the target one tile instead. If neither destination is accessible, the target remains in its original location. This effect occurs immediately upon entering FleeingState, separately from its ordinary escape behaviour.
Key classes and interfaces:
FleeingState
CreatureState
ZombieSensor
FleeBehaviour
TwoStepMoveAction
ZombieTarget

Contribution Log
https://docs.google.com/spreadsheets/d/1VugVD21tl_u1xxZpuDhpY3c8Dqlafoqd6AH90nrcc5E/edit?usp=sharing

