package game.quest;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;


public class ActivateObjectiveAction extends Action {
    private final QuestManager manager;
    private final QuestObjectiveGround objective;
    private final String name;


    public ActivateObjectiveAction(QuestManager manager, QuestObjectiveGround objective, String name) {
        this.manager = manager;
        this.objective = objective;
        this.name = name;
    }


    @Override
    public String execute(Actor actor, GameMap map) {
        return manager.activate(actor, map, objective);
    }


    @Override
    public String menuDescription(Actor actor) { return "Complete objective: " + name; }
}
