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

    /** Creates a fresh session with the contract's configured turn limit. */
    QuestSession(QuestDefinition definition) {
        this.definition = Objects.requireNonNull(definition, "definition");
        this.turnsRemaining = definition.turnLimit();
    }

    /** Returns the immutable contract configuration for this session. */
    QuestDefinition definition() { return definition; }

    /** Returns the remaining number of arena turns. */
    int turnsRemaining() { return turnsRemaining; }

    /** Returns the number of objectives completed so far. */
    int completedObjectives() { return completedObjectives; }

    /** Returns the number of registered objectives. */
    int totalObjectives() { return objectives.size(); }

    /** Registers an objective created during arena preparation. */
    void addObjective(QuestObjectiveGround objective) {
        objectives.add(Objects.requireNonNull(objective, "objective"));
    }

    /** Tests membership without exposing the mutable objective collection. */
    boolean contains(QuestObjectiveGround objective) {
        return objectives.contains(objective);
    }

    /** Validates ordered objectives while permitting any order for other contracts. */
    boolean isNextObjective(QuestObjectiveGround objective) {
        int index = objectives.indexOf(objective);
        return index >= 0 && (!definition.orderedObjectives() || index == completedObjectives);
    }

    /** Checks whether the next activation will finish this contract. */
    boolean isFinalObjective() {
        return !objectives.isEmpty() && completedObjectives + 1 == objectives.size();
    }

    /** Records a completed objective after the coordinator validates its location. */
    void completeObjective() {
        if (completedObjectives >= objectives.size()) {
            throw new IllegalStateException("There are no remaining objectives");
        }
        completedObjectives++;
    }

    /** Whether the session's objectives have all been completed. */
    boolean isComplete() {
        return !objectives.isEmpty() && completedObjectives == objectives.size();
    }

    /** Consumes one active arena turn; the coordinator controls when time advances. */
    void tick() {
        if(firstArenaTick){
            firstArenaTick = false;
            return;
        }
        if (turnsRemaining > 0){
            turnsRemaining--;
        }
    }

    /** Whether no arena turns remain. */
    boolean hasExpired() {
        return turnsRemaining == 0;
    }
}