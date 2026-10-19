package game.quest;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;


public class QuestJournal extends Item {
    private final QuestManager manager;


    public QuestJournal(QuestManager manager) {
        super("Quest Journal", 'j');
        this.manager = manager;
    }


    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (manager.isActiveFor(owner, map)) {
            actions.add(new QuestAbandonAction(manager));
        }
        return actions;
    }
}
