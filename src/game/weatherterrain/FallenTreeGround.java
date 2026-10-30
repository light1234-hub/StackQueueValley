package game.weatherterrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.Dirt;

public class FallenTreeGround extends WeatherGround
{
    private static final int LIFETIME_TURNS = 5;
    private int turnsLeft = LIFETIME_TURNS;

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

        if (location.containsAnActor())
        {
            Actor victim = location.getActor();
            int currentHealth = victim.getStatistic(ActorStatistics.HEALTH);
            victim.hurt(currentHealth);

            if(!victim.isConscious())
            {
                victim.unconscious(null,location.map());
            }
        }
        turnsLeft--;

        if (turnsLeft <= 0 && location.getGround() == this)
        {
            location.setGround(getOriginalGround());
        }
    }
}
