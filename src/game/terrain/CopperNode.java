package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.ToolAbility;
import game.blueprint.Breakable;
import game.blueprint.Chargeable;
import game.farmeraction.BreakAction;
import game.item.CopperItem;


public class CopperNode extends Ground implements Breakable {

    public CopperNode() {
        super('C', "Copper node");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public String breakBlock(Actor actor, Location location) {
        location.addItem(new CopperItem());
        location.setGround(new Dirt());

        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Chargeable chargeableGround = adjacent.getGroundAs(Chargeable.class);
            if (chargeableGround != null) {
                chargeableGround.charge();
            }
        }

        return actor + " breaks the copper node and a copper drops out. The surrounding ground crackles with charge!";
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
        return actions;
    }
}