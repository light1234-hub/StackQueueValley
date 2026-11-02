package game.quest;

/** Describes the persistent world effects of finishing a quest. */
public interface QuestConsequence {

    /**
     * Applies persistent changes to the farm after a successful quest.
     *
     * @param world controlled access to persistent farm modifications
     * @return description of the successful world changes
     */
    String onSuccess(QuestWorldContext world);

    /**
     * Applies persistent changes to the farm after a failed or abandoned quest.
     *
     * @param world controlled access to persistent farm modifications
     * @return description of the failure effects
     */
    String onFailure(QuestWorldContext world);
}
