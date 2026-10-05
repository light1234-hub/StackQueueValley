package game.map;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actor.*;
import game.item.AlienDevice;
import game.item.ParsnipItem;
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
        farmer.getInventory().add(device);

        minesMap.addActor(new Bat(), minesMap.at(3, 2));
        minesMap.addActor(new Slime(), minesMap.at(6, 3));
        minesMap.addActor(new CaveGhost(), minesMap.at(7, 4));
        /**
        // TEMP HEALING TEST
        Zombie zombie = new Zombie();
        zombie.hurt(60);

        inheritedFarmMap.addActor(
                zombie,
                inheritedFarmMap.at(2, 4)
        );

        inheritedFarmMap.at(1, 4).addItem(new ParsnipItem());
        inheritedFarmMap.at(2, 1).addItem(new ParsnipItem());
         **/
    }
}
