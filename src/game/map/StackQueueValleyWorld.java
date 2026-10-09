package game.map;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actor.*;
import game.item.AlienDevice;
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
        // REQ2: new ground types that can appear directly in an ASCII map
        groundCreator.registerGround('R', Rock::new);
        groundCreator.registerGround('C', CopperNode::new);
        groundCreator.registerGround('g', Geode::new);
        groundCreator.registerGround('H', Ladder::new);
        groundCreator.registerGround('O', TeleportationCircle::new);

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

        inheritedFarmMap.at(3, 2).setGround(new ParsnipCrop());
        inheritedFarmMap.at(4, 2).setGround(new StrawberryCrop());
        inheritedFarmMap.at(6, 2).setGround(new CauliflowerCrop());
        inheritedFarmMap.at(7, 2).setGround(new MelonCrop());

        // REQ2: place the farm-side ladder and teleportation circle. Set with
        // .setGround() the same way crops are, rather than baking them into
        // the ASCII strings above, so the original farm layout stays untouched.
        inheritedFarmMap.at(0, 5).setGround(new Ladder());
        inheritedFarmMap.at(9, 6).setGround(new TeleportationCircle());

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

        // --- Farmer setup ---
        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, inheritedFarmMap.at(5, 3));
        inheritedFarmMap.addActor(new Merchant(), inheritedFarmMap.at(8, 5));

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

        // REQ5: the zombie is a hostile creature, so it lives in the mines with the others
        minesMap.addActor(new Zombie(), minesMap.at(2, 4));
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