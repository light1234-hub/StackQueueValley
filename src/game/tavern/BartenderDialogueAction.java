package game.tavern;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/** Presents one reply in the Tavern bartender's branching conversation. */
public class BartenderDialogueAction extends Action {
    private final BartenderDialogue dialogue;
    private final BartenderDialogue.Topic topic;

    /**
     * Creates a selectable bartender dialogue reply.
     *
     * @param dialogue the conversation controller
     * @param topic the reply that the actor can select
     * @throws IllegalArgumentException if either argument is null
     */
    public BartenderDialogueAction(BartenderDialogue dialogue, BartenderDialogue.Topic topic) {
        if (dialogue == null || topic == null) {
            throw new IllegalArgumentException("Dialogue and topic are required");
        }
        this.dialogue = dialogue;
        this.topic = topic;
    }

    /**
     * Executes a reply, producing dialogue rather than changing quest state.
     *
     * @param actor the actor speaking to the bartender
     * @param map the current game map
     * @return the bartender's answer or a reason conversation is unavailable
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return dialogue.choose(actor, map, topic);
    }

    /**
     * Returns the player's dialogue choice shown in the engine menu.
     *
     * @param actor the actor choosing a reply
     * @return the menu wording for this dialogue topic
     */
    @Override
    public String menuDescription(Actor actor) {
        return switch (topic) {
            case GREET -> "Talk to the bartender";
            case ASK_CONTRACTS -> "Bartender, got any contracts?";
            case ASK_WAGER -> "What if I can't afford a contract?";
            case GO_BACK -> "Ask the bartender something else";
            case SAY_GOODBYE -> "End the conversation";
        };
    }
}
