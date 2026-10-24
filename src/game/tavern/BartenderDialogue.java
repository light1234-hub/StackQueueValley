package game.tavern;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.quest.QuestManager;

import java.util.stream.Collectors;

/**
 * Tracks a single branching conversation with the Tavern bartender.
 * Dialogue changes only which menu actions are shown; it does not charge
 * coins, start quests, or run the drinking contest.
 */
public class BartenderDialogue {

    /** The stages of a conversation with the bartender. */
    public enum Stage {
        CLOSED, GREETING, CONTRACTS, WAGER
    }

    /** The choices offered by the bartender's dialogue menu. */
    public enum Topic {
        GREET, ASK_CONTRACTS, ASK_WAGER, GO_BACK, SAY_GOODBYE
    }

    private final QuestManager quests;
    private final DrinkingContest contest;
    private Actor speaker;
    private GameMap conversationMap;
    private Stage stage = Stage.CLOSED;

    /**
     * Creates a dialogue controller for the bartender's quest service.
     *
     * @param quests manager that validates Tavern interactions and supplies contracts
     * @param contest controller that determines when the wager is available
     * @throws IllegalArgumentException if either dependency is null
     */
    public BartenderDialogue(QuestManager quests, DrinkingContest contest) {
        if (quests == null || contest == null) {
            throw new IllegalArgumentException("Quest manager and drinking contest are required");
        }
        this.quests = quests;
        this.contest = contest;
    }

    /**
     * Determines the current conversation stage for an actor.
     *
     * @param actor the actor inspecting the bartender menu
     * @param map the actor's current map
     * @return the current stage, or CLOSED for anyone not in this conversation
     */
    public Stage stageFor(Actor actor, GameMap map) {
        if (speaker != actor || conversationMap != map || !quests.canInteractAtBar(actor, map)
                || quests.hasActiveQuest()) {
            return Stage.CLOSED;
        }
        return stage;
    }

    /**
     * Handles a dialogue selection and updates the active conversation stage.
     * The actor must remain beside the bartender throughout the conversation.
     *
     * @param actor the actor selecting a reply
     * @param map the current Tavern map
     * @param topic the selected dialogue topic
     * @return the bartender's response or an explanation of why it is unavailable
     */
    public String choose(Actor actor, GameMap map, Topic topic) {
        if (topic == null || !quests.canInteractAtBar(actor, map) || quests.hasActiveQuest()) {
            return "You must stand beside the bartender to have this conversation.";
        }
        if (topic == Topic.GREET) {
            if (stageFor(actor, map) != Stage.CLOSED) {
                return "Bartender: One question at a time, friend!";
            }
            speaker = actor;
            conversationMap = map;
            stage = Stage.GREETING;
            return "Bartender: Well, well! Looking for honest work? Wrong tavern, friend. "
                    + "I've got contracts, and if your purse is light, I've got a wager.";
        }
        if (stageFor(actor, map) == Stage.CLOSED) {
            return "Speak to the bartender before choosing a reply.";
        }
        switch (topic) {
            case ASK_CONTRACTS -> {
                if (stage != Stage.GREETING) return "Go back to the main conversation first.";
                stage = Stage.CONTRACTS;
                String offers = quests.contracts().stream()
                        .map(contract -> contract.title() + " (" + contract.entryFee() + " coins)")
                        .collect(Collectors.joining("; "));
                return "Bartender: I've got these contracts: " + offers + ". "
                        + "Choose carefully: the fee is paid up front, and failure has consequences.";
            }
            case ASK_WAGER -> {
                if (stage != Stage.GREETING) return "Go back to the main conversation first.";
                if (!contest.canChallenge(actor, map)) {
                    return "Bartender: The wager isn't available right now.";
                }
                stage = Stage.WAGER;
                return "Bartender: Short on coin? Outdrink me in three rounds and I'll send you "
                        + "on a RANDOM contract for free. Sip for one cup, or chug for two with "
                        + "a chance of blacking out. You might draw the hardest job of all. "
                        + "Lose, and you'll be four stamina worse off!";
            }
            case GO_BACK -> {
                if (stage != Stage.CONTRACTS && stage != Stage.WAGER) {
                    return "Bartender: You're already talking to me, friend.";
                }
                stage = Stage.GREETING;
                return "Bartender: All right. Contracts, drinks, or a quiet exit?";
            }
            case SAY_GOODBYE -> {
                close();
                return "Bartender: Come back when you're feeling brave, or wealthy!";
            }
            default -> {
                return "Bartender: I didn't catch that.";
            }
        }
    }

    /**
     * Ends the conversation if its speaker has left the bar or started a quest.
     * Called on the bartender's turn to avoid keeping stale menu state.
     */
    public void closeIfInterrupted() {
        if (speaker != null && (quests.hasActiveQuest()
                || !quests.canInteractAtBar(speaker, conversationMap))) {
            close();
        }
    }

    /** Ends the current conversation and resets the dialogue menu. */
    public void close() {
        speaker = null;
        conversationMap = null;
        stage = Stage.CLOSED;
    }
}
