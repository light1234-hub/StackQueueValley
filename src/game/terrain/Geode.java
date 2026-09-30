package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.blueprint.Breakable;
import game.farmeraction.BreakAction;
import game.farmeraction.PickUpGeodeAction;

/**
 * A geode lying on the ground. Unlike Rock and CopperNode, a geode can also
 * be picked up and carried instead of broken immediately -- see
 * PickUpGeodeAction and game.item.GeodeItem. Breaking it, whether from the
 * ground or from inventory, is resolved identically via GeodeMechanics.
 */
public class Geode extends Ground implements Breakable {

    public Geode() {
        super('g', "Geode");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public String breakBlock(Actor actor, Location location) {
        location.setGround(new Dirt());
        return GeodeMechanics.resolve(actor, location);
    }

    @Override
    public int breakStaminaCost() {
        return 0;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new BreakAction(this, location));
        actions.add(new PickUpGeodeAction(location));
        return actions;
    }
}
