package game.tavern;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/** Performs a single player-selected round in the active drinking contest. */
public class DrinkRoundAction extends Action {
    private final DrinkingContest contest;
    private final DrinkChoice choice;

    /**
     * @param contest the active Tavern contest
     * @param choice the Sip or Chug strategy selected for this round
     */
    public DrinkRoundAction(DrinkingContest contest, DrinkChoice choice) {
        if (contest == null || choice == null) {
            throw new IllegalArgumentException("Contest and drink choice are required");
        }
        this.contest = contest;
        this.choice = choice;
    }

    /**
     * @param actor the drinking contender
     * @param map the contender's current Tavern map
     * @return score and any outcome of this round
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return contest.drink(actor, map, choice);
    }

    /**
     * @param actor the contender choosing a drinking strategy
     * @return choice, points and risk shown in the action menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return choice.menuText();
    }
}
