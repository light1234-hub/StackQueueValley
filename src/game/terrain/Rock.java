package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Breakable;
import game.farmeraction.BreakAction;
import game.item.StoneItem;

import java.util.List;
import java.util.Random;

/**
 * An impassable rock found in the mines. Must be broken with a pickaxe to
 * clear the way.
 */
public class Rock extends Ground implements Breakable {
    private final Random random = new Random();

    public Rock() {
        super('R', "Rock");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Breaking always drops a stone and turns the tile to dirt. There's also
     * a 50% chance of a shockwave that throws the breaking actor onto a
     * random tile adjacent to the rock.
     */
    @Override
    public String breakBlock(Actor actor, Location location) {
        location.addItem(new StoneItem());
        location.setGround(new Dirt());

        String message = actor + " breaks the rock and a stone drops out.";

        if (random.nextBoolean()) {
            List<Location> adjacent = location.getExits().stream()
                    .map(Exit::getDestination)
                    .toList();
            if (!adjacent.isEmpty()) {
                Location shockDestination = adjacent.get(random.nextInt(adjacent.size()));
                if (shockDestination.canActorEnter(actor)) {
                    location.map().moveActor(actor, shockDestination);
                    message += " A shockwave throws " + actor + " onto a nearby tile!";
                }
            }
        }
        return message;
    }

    @Override
    public int breakStaminaCost() {
        return 0;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new BreakAction(this, location));
        return actions;
    }
}
