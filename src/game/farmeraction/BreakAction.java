package game.farmeraction;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.ToolAbility;
import game.blueprint.Breakable;


public class BreakAction extends Action {
    private final Breakable target;
    private final Location targetLocation;

    /**
     * Creates a break action for a piece of breakable ground.
     *
     * @param target the breakable object (rock, copper node, geode)
     * @param targetLocation the location of that object
     */
    public BreakAction(Breakable target, Location targetLocation) {
        this.target = target;
        this.targetLocation = targetLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(ToolAbility.MINE)) {
            return actor + " needs a Pickaxe to break this.";
        }
        if (actor.getStatistic(ActorStatistics.STAMINA) < target.breakStaminaCost()) {
            return actor + " is too tired to break this.";
        }
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, target.breakStaminaCost());
        return target.breakBlock(actor, targetLocation);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " breaks the " + target;
    }
}
