package game.actor;

import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Slime extends HostileCreature {
    public Slime() { super("Slime", 's', 150); }
    @Override protected int damage() { return 15; }
    @Override protected int hitChance() { return 50; }
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
