package game.tavern;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.quest.QuestManager;

/**
 * Tavern exit that cannot be used to bypass the completion or failure handling
 * of an active quest.
 */
public class QuestExitDoor extends TavernDoor {
    private final QuestManager manager;

    /**
     * Creates an exit door governed by the quest manager.
     *
     * @param destinationName display name of the linked destination
     * @param manager manager that tracks active contracts
     */
    public QuestExitDoor(String destinationName, QuestManager manager) {
        super(destinationName);
        this.manager = manager;
    }

    /**
     * Hides travel while a quest remains active; otherwise delegates to the normal door.
     *
     * @param actor actor interacting with the door
     * @param location location of the doorway
     * @param direction direction from the actor to the door
     * @return available travel actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        if (manager.hasActiveQuest()) {
            return new ActionList();
        }
        return super.allowableActions(actor, location, direction);
    }
}
