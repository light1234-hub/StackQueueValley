package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.ToolAbility;
import game.blueprint.Breakable;
import game.farmeraction.BreakAction;
import game.farmeraction.PickUpGeodeAction;


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
        if (actor.hasAbility(ToolAbility.MINE)) {
            actions.add(new BreakAction(this, location));
        }
        actions.add(new PickUpGeodeAction(location));
        return actions;
    }
}