package game.quest;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public class QuestAbandonAction extends Action {
    private final QuestManager manager;

    public QuestAbandonAction(QuestManager manager) { this.manager = manager; }


    @Override
    public String execute(Actor actor, GameMap map) { return manager.abandon(actor, map); }


    @Override
    public String menuDescription(Actor actor) {
        return "Abandon contract (failure consequences, return to Tavern)";
    }
}
