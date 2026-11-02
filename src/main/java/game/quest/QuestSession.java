package game.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owns progress for one active contract: registered objectives, completion order,
 * and the remaining deadline. Farm travel and arena lifecycle are managed elsewhere.
 */
final class QuestSession {
    private final QuestDefinition definition;
    private final List<QuestObjectiveGround> objectives = new ArrayList<>();
    private int completedObjectives;
    private int turnsRemaining;
    private boolean firstArenaTick = true;

    /**
     * Creates progress state for one quest definition with its configured deadline.
     *
     * @param definition quest definition associated with this session
     */
    QuestSession(QuestDefinition definition) {
        this.definition = Objects.requireNonNull(definition, "definition");
        this.turnsRemaining = definition.turnLimit();
    }

    /**
     * Returns the contract definition for this session.
     *
     * @return quest definition associated with the session
     */
    QuestDefinition definition() { return definition; }

    /**
     * Returns the remaining quest turns.
     *
     * @return number of turns remaining in this session
     */
    int turnsRemaining() { return turnsRemaining; }

    /**
     * Returns the number of completed objectives.
     *
     * @return number of objectives completed so far
     */
    int completedObjectives() { return completedObjectives; }

    /**
     * Returns the total number of registered objectives.
     *
     * @return number of registered quest objectives
     */
    int totalObjectives() { return objectives.size(); }

    /**
     * Adds an objective to the active session.
     *
     * @param objective objective to add to the session
     */
    void addObjective(QuestObjectiveGround objective) {
        objectives.add(Objects.requireNonNull(objective, "objective"));
    }

    /**
     * Checks whether this session contains the given objective.
     *
     * @param objective objective whose membership is being checked
     * @return true if this session contains the objective
     */
    boolean contains(QuestObjectiveGround objective) {
        return objectives.contains(objective);
    }

    /**
     * Checks whether the objective is eligible under the contract ordering rules.
     *
     * @param objective objective being checked against the completion order
     * @return true if this objective can be activated next
     */
    boolean isNextObjective(QuestObjectiveGround objective) {
        int index = objectives.indexOf(objective);
        return index >= 0 && (!definition.orderedObjectives() || index == completedObjectives);
    }

    /**
     * Checks whether one more objective would complete the quest.
     *
     * @return true if one more activation would complete the quest
     */
    boolean isFinalObjective() {
        return !objectives.isEmpty() && completedObjectives + 1 == objectives.size();
    }

    /**
     * Records the completion of one remaining objective.
     *
     * @throws IllegalStateException if there are no remaining objectives
     */
    void completeObjective() {
        if (completedObjectives >= objectives.size()) {
            throw new IllegalStateException("There are no remaining objectives");
        }
        completedObjectives++;
    }

    /**
     * Checks whether all registered objectives have been completed.
     *
     * @return true if every registered objective has been completed
     */
    boolean isComplete() {

        return !objectives.isEmpty() && completedObjectives == objectives.size();
    }

    /**
     * Advances the quest timer, excluding its initial arena tick.
     */
    void tick() {
        if(firstArenaTick){
            firstArenaTick = false;
            return;
        }
        if (turnsRemaining > 0){
            turnsRemaining--;
        }
    }

    /**
     * Checks whether the quest deadline has reached zero.
     *
     * @return true if no quest turns remain
     */
    boolean hasExpired() {

        return turnsRemaining == 0;
    }
}