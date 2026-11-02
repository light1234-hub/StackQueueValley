package game.map;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Slime;
import game.actor.Bat;
import game.actor.Wizard;
import game.terrain.QuarryCauldron;
import game.terrain.OrchardCauldron;
import game.terrain.TamingCauldron;
import game.terrain.WishingWell;

import java.util.List;

/** Factory for the Alchemy Valley map (not a World subclass). Call populate() after addGameMap. */
public class AlchemyValleyWorld {
    private static final List<String> LAYOUT = List.of(
            "...............",
            "...............",
            "...R.......R...",
            "......R.R......",
            "...............",
            "...R.......R...",
            "...............",
            "...............",
            "...............");

    private final GameMap map;
    private final WishingWell returnWell = new WishingWell(0);

    /** @param groundCreator the existing creator that maps '.' and 'R' to terrain (the map edge is the boundary) */
    public AlchemyValleyWorld(GroundCreator groundCreator) throws GameEngineException {
        map = new GameMap("Alchemy Valley", groundCreator, LAYOUT);
        map.at(4, 1).setGround(new OrchardCauldron());
        map.at(7, 1).setGround(new TamingCauldron());
        map.at(10, 1).setGround(new QuarryCauldron());
        map.at(13, 7).setGround(returnWell);
    }

    public GameMap getMap() {
        return map;
    }

    public WishingWell getReturnWell() {
        return returnWell;
    }

    /** Where the farmer lands (beside the return well). */
    public Location arrivalLocation() {
        return map.at(12, 7);
    }

    /** Must be called after the map has been added to the World. */
    public void populate() throws GameEngineException {
        map.at(7, 5).addActor(new Wizard());
        map.at(3, 6).addActor(new Slime());
        map.at(9, 6).addActor(new Bat());
    }
}