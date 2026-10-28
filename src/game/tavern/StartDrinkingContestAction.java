package game.tavern;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/** Starts a drinking wager after the player discusses it with the bartender. */
public class StartDrinkingContestAction extends Action {
    private final DrinkingContest contest;
    private final BartenderDialogue dialogue;

    /**
     * Creates an action for starting the wager after the wager conversation.
     *
     * @param contest the shared drinking contest
     * @param dialogue the conversation controller
     * @throws IllegalArgumentException if either argument is null
     */
    public StartDrinkingContestAction(DrinkingContest contest, BartenderDialogue dialogue) {
        if (contest == null || dialogue == null) {
            throw new IllegalArgumentException("Contest and dialogue are required");
        }
        this.contest = contest;
        this.dialogue = dialogue;
    }

    /**
     * Starts the three-round drinking contest after validating the conversation.
     *
     * @param actor the contestant
     * @param map the current Tavern map
     * @return contest instructions or a refusal message
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (dialogue.stageFor(actor, map) != BartenderDialogue.Stage.WAGER) {
            return "Ask the bartender about the wager before challenging him.";
        }
        String message = contest.start(actor, map);
        if (contest.inProgress()) dialogue.close();
        return message;
    }

    /**
     * Provides the menu option for beginning the wager.
     *
     * @param actor the contestant
     * @return the challenge description
     */
    @Override
    public String menuDescription(Actor actor) {
        return "All right, bartender. Let's drink! (3 rounds, random free contract)";
    }
}
