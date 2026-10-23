package game.quest;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An impassable quest objective. An authorised Farmer can activate it while adjacent,
 * and the manager validates that it still belongs to the current contract.
 */
public class QuestObjectiveGround extends Ground {
    private final QuestManager manager;
    private final Location location;
    private final String objectiveName;

    /**
     * Creates an objective bound to its manager and location.
     *
     * @param manager manager that validates and records completion
     * @param location location where the objective is placed
     * @param objectiveName objective text shown to the player
     */
    public QuestObjectiveGround(QuestManager manager, Location location, String objectiveName) {
        super('!', objectiveName);
        this.manager = manager;
        this.location = location;
        this.objectiveName = objectiveName;
    }

    /**
     * Prevents actors from walking onto an objective tile.
     *
     * @param actor actor attempting to enter
     * @return always {@code false}
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Adds an activation action when an authorised actor is adjacent.
     *
     * @param actor actor interacting with the objective
     * @param here location of this ground
     * @param direction direction from the actor to the objective
     * @return available objective actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location here, String direction) {
        ActionList actions = super.allowableActions(actor, here, direction);
        if (!direction.isEmpty() && manager.canInteract(actor, this)) {
            actions.add(new ActivateObjectiveAction(manager, this, objectiveName));
        }
        return actions;
    }

    /**
     * Returns the location tied to this objective.
     *
     * @return the objective location
     */
    public Location location() {
        return location;
    }
}
