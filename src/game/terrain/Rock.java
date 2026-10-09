package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.ToolAbility;
import game.blueprint.Breakable;
import game.farmeraction.BreakAction;
import game.farmeraction.SafeArrival;
import game.item.StoneItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Rock extends Ground implements Breakable {
    private final Random random = new Random();

    public Rock() {
        super('R', "Rock");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }


    @Override
    public String breakBlock(Actor actor, Location location) {
        location.addItem(new StoneItem());
        location.setGround(new Dirt());

        String message = actor + " breaks the rock and a stone drops out.";

        if (random.nextBoolean()) {
            // Only tiles the actor can actually land on are candidates, so the
            // throw never fails just because a random pick happened to be blocked.
            List<Location> landingSpots = new ArrayList<>();
            for (Exit exit : location.getExits()) {
                Location candidate = exit.getDestination();
                if (SafeArrival.isFree(candidate, actor)) {
                    landingSpots.add(candidate);
                }
            }
            if (!landingSpots.isEmpty()) {
                Location shockDestination = landingSpots.get(random.nextInt(landingSpots.size()));
                location.map().moveActor(actor, shockDestination);
                message += " A shockwave throws " + actor + " onto a nearby tile!";
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
        if (actor.hasAbility(ToolAbility.MINE)) {
            actions.add(new BreakAction(this, location));
        }
        return actions;
    }
}