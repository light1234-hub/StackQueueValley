package game.actor;

import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Mine creature that may spawn another slime whenever it is attacked. */
public class Slime extends HostileCreature {
    /** Creates a slime with 150 health points. */
    public Slime() { super("Slime", 's', 150); }
    /** @return the slime's 15 attack damage */
    @Override protected int damage() { return 15; }
    /** @return the slime's 50 percent hit chance */
    @Override protected int hitChance() { return 50; }

    /** Has a 50 percent chance to spawn a slime on a free adjacent tile. */
    @Override public void onAttacked(Location location, boolean killed) {
        if (new Random().nextBoolean()) {
            List<Location> choices = new ArrayList<>();
            for (edu.monash.fit2099.engine.positions.Exit exit : location.getExits()) {
                Location next = exit.getDestination();
                if (!next.containsAnActor() && next.canActorEnter(this)) choices.add(next);
            }
            if (!choices.isEmpty()) {
                try {
                    choices.get(new Random().nextInt(choices.size())).addActor(new Slime());
                } catch (edu.monash.fit2099.engine.GameEngineException ignored) {
                    // Another actor may have occupied the tile during the same turn.
                }
            }
        }
    }
}
