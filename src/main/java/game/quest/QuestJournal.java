package game.quest;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Item carried by the Farmer that exposes the abandonment action while a quest is active.
 */
public class QuestJournal extends Item {
    private final QuestManager manager;

    /**
     * Creates a journal linked to the quest manager.
     *
     * @param manager manager whose current contract can be abandoned
     */
    public QuestJournal(QuestManager manager) {
        super("Quest Journal", 'j');
        this.manager = manager;
    }

    /**
     * Offers an abandonment option only to the Farmer participating in the active quest.
     *
     * @param owner actor whose inventory contains this journal
     * @param map current game map
     * @return available journal actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (manager.isActiveFor(owner, map)) {
            actions.add(new QuestAbandonAction(manager));
        }
        return actions;
    }
}
