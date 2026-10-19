package game.tavern;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.ability.TavernAbility;
import game.quest.QuestAbandonAction;
import game.quest.QuestManager;


public class QuestExitDoor extends TavernDoor {
    private final QuestManager manager;


    public QuestExitDoor(String destinationName, QuestManager manager) {
        super(destinationName);
        this.manager = manager;
    }


    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        if (manager.hasActiveQuest()) {
            ActionList actions = new ActionList();
            if (!direction.isEmpty() && actor.hasAbility(TavernAbility.CAN_USE_TAVERN)) {
                actions.add(new QuestAbandonAction(manager));
            }
            return actions;
        }
        return super.allowableActions(actor, location, direction);
    }
}
