package game.weatherterrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.item.CopperItem;
import game.item.StoneItem;
import game.terrain.Dirt;
import game.weatherblueprint.WeatherRecoverable;

import java.util.Random;

public class FallenTreeGround extends WeatherGround implements WeatherRecoverable
{
    private static final int LIFETIME_TURNS = 5;
    private int turnsLeft = LIFETIME_TURNS;
    private static final int COPPER_AMOUNT = 5;
    private static final int STONE_AMOUNT = 10;
    private final Random random = new Random();

    public FallenTreeGround()
    {
        super('T',"Fallen Tree", new Dirt());
    }

    @Override
    public boolean canActorEnter(Actor actor)
    {
        return false;
    }

    @Override
    public void tick(Location location)
    {
        turnsLeft--;

        if (turnsLeft <= 0 && location.getGround() == this)
        {
            location.setGround(getOriginalGround());
        }
    }
    /**
     * Recovers resources from the fallen tree and clears the terrain.
     * When recovery succeeds, the fallen tree is replaced with Dirt.
     * There is a 50% chance of producing five Copper items and a
     * 50% chance of producing ten Stone items. The resulting items
     * are placed at the recovered location.
     * @param location the location where recovery is attempted
     * @return true if the fallen tree was successfully recovered;
     *         false if the location is invalid or occupied by an actor
     */
    @Override
    public boolean recover(Location location){
        if(location.getGround() != this || location.containsAnActor()){
            return false;
        }

        boolean dropCopper = random.nextBoolean();
        location.setGround(getOriginalGround());

        if(dropCopper){
            for(int i = 0; i<COPPER_AMOUNT; i++){
                location.addItem(new CopperItem());
            }
        } else{
            for(int i = 0; i < STONE_AMOUNT; i++){
                location.addItem(new StoneItem());
            }
        }
        return true;
    }

}
