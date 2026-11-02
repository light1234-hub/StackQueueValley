package game.req3tests;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/** Menu action that asks the quest manager to activate one objective. */
public class ActivateObjectiveAction extends Action {
    private final QuestManager manager;
    private final QuestObjectiveGround objective;
    private final String name;

    /**
     * Creates an action for one quest objective.
     *
     * @param manager manager that validates quest progress
     * @param objective objective to activate
     * @param name display name of the objective
     */
    public ActivateObjectiveAction(QuestManager manager,
                                   QuestObjectiveGround objective, String name) {
        this.manager = manager;
        this.objective = objective;
        this.name = name;
    }

    /**
     * Executes the objective activation request.
     *
     * @param actor actor choosing the action
     * @param map current game map
     * @return a message describing the outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return manager.activate(actor, map, objective);
    }

    /**
     * Returns the action text displayed in the menu.
     *
     * @param actor actor choosing the action
     * @return objective activation description
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Complete objective: " + name;
    }
}
