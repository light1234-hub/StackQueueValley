package game.map;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actor.*;
import game.arena.ArenaQuest;
import game.item.*;
import game.quest.*;
import game.tavern.*;
import game.terrain.*;
import game.tool.Pickaxe;

import java.util.Arrays;
import java.util.List;

public class StackQueueValleyWorld extends World {

    /**
     * Constructor.
     *
     * @param display the Display that will display this World.
     */
    public StackQueueValleyWorld(Display display) {
        super(display);
    }

    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('-', TavernFloor::new);
        // REQ2: new ground types that can appear directly in an ASCII map
        groundCreator.registerGround('R', Rock::new);
        groundCreator.registerGround('C', CopperNode::new);
        groundCreator.registerGround('g', Geode::new);
        groundCreator.registerGround('H', Ladder::new);
        groundCreator.registerGround('O', TeleportationCircle::new);
        groundCreator.registerGround('#', TavernWall::new);
        // --- Farm map (unchanged layout from REQ1) ---
        List<String> inheritedFarm = Arrays.asList(
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                ".........."
        );

        GameMap inheritedFarmMap = new GameMap("Inherited Farm", groundCreator, inheritedFarm);
        this.addGameMap(inheritedFarmMap);

        // REQ1: the farm starts with every plant stage already established.
        //inheritedFarmMap.at(1, 2).setGround(new StrawberrySeedling());
        //inheritedFarmMap.at(2, 2).setGround(new StrawberrySprout());
        inheritedFarmMap.at(3, 2).setGround(new StrawberryCrop());
        //inheritedFarmMap.at(4, 2).setGround(new CauliflowerSprout());
        inheritedFarmMap.at(5, 2).setGround(new CauliflowerCrop());
        //inheritedFarmMap.at(6, 2).setGround(new MelonSprout());
        inheritedFarmMap.at(7, 2).setGround(new MelonCrop());
        //inheritedFarmMap.at(8, 2).setGround(new TomatoCrop());
        inheritedFarmMap.at(9, 2).setGround(new ParsnipCrop());
        /**
        // REQ2: place the farm-side ladder and teleportation circle. Set with
        // .setGround() the same way crops are, rather than baking them into
        // the ASCII strings above, so the original farm layout stays untouched.
        inheritedFarmMap.at(0, 5).setGround(new Ladder());
        inheritedFarmMap.at(9, 6).setGround(new TeleportationCircle());

        // REQ5: Zombie on the farm
        //inheritedFarmMap.addActor(new Zombie(), inheritedFarmMap.at(2, 4));
        */

        /**
        inheritedFarmMap.at(1, 4).addItem(new ParsnipItem());
        inheritedFarmMap.at(0, 4).addItem(new MelonItem());
        inheritedFarmMap.at(1, 3).addItem(new StrawberryItem(false));
        inheritedFarmMap.at(3, 3).addItem(new CauliflowerItem());
        inheritedFarmMap.at(4, 4).addItem(new StrawberryItem(false));
        inheritedFarmMap.at(3, 5).addItem(new ParsnipItem());
         */
        /**
        // --- Mines map (new for REQ2) ---
        List<String> mines = Arrays.asList(
                "RRRRRRRRRR",
                "R........R",
                "R.C....g.R",
                "R........R",
                "R....R...R",
                "H........R",
                "RRRRRRRRRO"
        );

        GameMap minesMap = new GameMap("The Mines", groundCreator, mines);
        this.addGameMap(minesMap);

        // REQ2: link the farm and mines ladders/circles together. This must
        // happen after both maps exist, since each side needs a real
        // Location from the other map.
        Location farmLadderLocation = inheritedFarmMap.at(0, 5);
        Location minesLadderLocation = minesMap.at(0, 5);
        ((Ladder) farmLadderLocation.getGround()).setDestination(minesLadderLocation);
        ((Ladder) minesLadderLocation.getGround()).setDestination(farmLadderLocation);

        Location farmCircleLocation = inheritedFarmMap.at(9, 6);
        Location minesCircleLocation = minesMap.at(9, 6);
        ((TeleportationCircle) farmCircleLocation.getGround()).setDestination(minesCircleLocation);
        ((TeleportationCircle) minesCircleLocation.getGround()).setDestination(farmCircleLocation);

        // Extra travel points: three ladders and three teleportation circles in total.
        // Ladders: (0,5) above, plus these two pairs.
        linkLadders(inheritedFarmMap.at(0, 1), minesMap.at(1, 1));
        linkLadders(inheritedFarmMap.at(9, 1), minesMap.at(8, 1));
        // Circles: (9,6) above, plus these two pairs.
        linkCircles(inheritedFarmMap.at(3, 6), minesMap.at(4, 5));
        linkCircles(inheritedFarmMap.at(6, 0), minesMap.at(4, 1));
         */

        // --- Farmer setup ---
        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, inheritedFarmMap.at(5, 3));
        inheritedFarmMap.addActor(new Merchant(), inheritedFarmMap.at(8, 5));

        /**
        // REQ2: give the farmer a pickaxe and an alien device with one fixed
        // destination on each map.
        farmer.getInventory().add(new Pickaxe());

        AlienDevice device = new AlienDevice();
        device.addDestination(inheritedFarmMap.at(5, 3));
        device.addDestination(minesMap.at(5, 3));
        // REQ2: the device is found on the farm map, not carried from the start
        inheritedFarmMap.at(7, 4).addItem(device);

        minesMap.addActor(new Bat(), minesMap.at(3, 2));
        minesMap.addActor(new Slime(), minesMap.at(6, 3));
        minesMap.addActor(new CaveGhost(), minesMap.at(7, 4));
         **/
        // A3 REQ3 phase 1: the Tavern and one reusable Quest Arena.
        // Existing farm/mines maps and all A1/A2 travel links remain intact.
        List<String> tavernRows = Arrays.asList(
                "###########",
                "R---------R",
                "R---------R",
                "R---------R",
                "R---------R",
                "R---------R",
                "###########"
        );
        GameMap tavernMap = new GameMap("The Drunken Tavern", groundCreator, tavernRows);
        this.addGameMap(tavernMap);

        List<String> arenaRows = Arrays.asList(
                "###############",
                "R.............R",
                "R.............R",
                "R.............R",
                "R.............R",
                "R.............R",
                "R.............R",
                "R.............R",
                "###############"
        );
        GameMap arenaMap = new GameMap("Reusable Quest Arena", groundCreator, arenaRows);
        this.addGameMap(arenaMap);
        ArenaQuest arena = new ArenaQuest(arenaMap, farmer);

        // Doors are impassable furniture; the Farmer interacts from a neighbouring tile.
        TavernDoor farmDoor = new TavernDoor("The Drunken Tavern");
        TavernDoor tavernExit = new TavernDoor("Inherited Farm");
        inheritedFarmMap.at(5, 4).setGround(farmDoor);
        tavernMap.at(1, 2).setGround(tavernExit);
        farmDoor.linkTo(tavernMap.at(2, 2));
        tavernExit.linkTo(inheritedFarmMap.at(5, 3));

        // A3 REQ3: three paid contracts use the same arena and shared XP.
        List<QuestDefinition> contracts = List.of(
                new QuestDefinition("The Cursed Forest", 120, 280, 100, 45,
                        false, new CursedForestScenario(), new ForestConsequence()),
                new QuestDefinition("The Lost Survivors", 200, 450, 160, 55,
                        false, new RescueMissionScenario(), new RescueConsequence()),
                new QuestDefinition("The Ancient Relic", 350, 800, 260, 65,
                        true, new AncientRelicScenario(), new RelicConsequence())
        );
        Location bartenderLocation = tavernMap.at(6, 3);
        QuestManager quests = new QuestManager(farmer, arena, tavernMap.at(2, 2),
                bartenderLocation, inheritedFarmMap, contracts);
        QuestExitDoor arenaExit = new QuestExitDoor("The Drunken Tavern", quests);
        arenaMap.at(1, 2).setGround(arenaExit);
        arenaExit.linkTo(tavernMap.at(2, 2));
        tavernMap.addActor(new ContractBartender(quests), bartenderLocation);
        farmer.getInventory().add(new QuestJournal(quests));

    }

    /**
     * Places a ladder on each location and links the two so that each leads to the other.
     * Call this after both maps exist.
     */
    private void linkLadders(Location a, Location b) {
        a.setGround(new Ladder());
        b.setGround(new Ladder());
        ((Ladder) a.getGround()).setDestination(b);
        ((Ladder) b.getGround()).setDestination(a);
    }

    /**
     * Places a teleportation circle on each location and links the two so that each
     * leads to the other. Call this after both maps exist.
     */
    private void linkCircles(Location a, Location b) {
        a.setGround(new TeleportationCircle());
        b.setGround(new TeleportationCircle());
        ((TeleportationCircle) a.getGround()).setDestination(b);
        ((TeleportationCircle) b.getGround()).setDestination(a);
    }
}
