package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.item.StrawberryItem;
import game.item.TomatoItem;

import java.util.Random;

/** Impassable tree that drops a random Strawberry or Tomato every 2 turns. */
public class ArcaneTree extends Ground {
    private static final Random RNG = new Random();
    private int age;

    public ArcaneTree() {
        super('T', "Arcane Tree");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public void tick(Location location) {
        if (++age % 2 == 0) {
            location.addItem(RNG.nextBoolean() ? new StrawberryItem() : new TomatoItem());
        }
    }
}