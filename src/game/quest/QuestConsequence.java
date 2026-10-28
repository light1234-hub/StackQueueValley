package game.quest;

/** Describes the persistent world effects of finishing a quest. */
public interface QuestConsequence {
    /** @param world access to the persistent farm and its entities
     *  @return description of the successful changes */
    String onSuccess(QuestWorldContext world);
    /** @param world access to the persistent farm and its entities
     *  @return description of the failure changes */
    String onFailure(QuestWorldContext world);
}
