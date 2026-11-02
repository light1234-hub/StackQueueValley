package game.req3tests;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/** Menu action that abandons the active contract and applies its failure consequence. */
public class QuestAbandonAction extends Action {
    private final QuestManager manager;

    /**
     * Creates an abandonment action connected to the active quest manager.
     *
     * @param manager manager responsible for handling abandonment
     */
    public QuestAbandonAction(QuestManager manager) {
        this.manager = manager;
    }

    /**
     * Requests contract abandonment.
     *
     * @param actor actor choosing to abandon
     * @param map current map containing the actor
     * @return a message describing the abandonment result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return manager.abandon(actor, map);
    }

    /**
     * Returns the action text displayed in the menu.
     *
     * @param actor actor choosing the action
     * @return abandonment description
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Abandon contract (failure consequences, return to farm)";
    }
}
