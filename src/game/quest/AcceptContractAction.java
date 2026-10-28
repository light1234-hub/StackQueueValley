package game.quest;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/** Menu action to purchase and enter one selected Tavern contract. */
public class AcceptContractAction extends Action {
    private final QuestManager manager;
    private final QuestDefinition contract;

    /** @param manager the Tavern contract service
     *  @param contract the offered quest */
    public AcceptContractAction(QuestManager manager, QuestDefinition contract) {
        this.manager = manager;
        this.contract = contract;
    }

    /** Validates payment, prepares the quest and enters its arena. */
    @Override
    public String execute(Actor actor, GameMap map) { return manager.accept(actor, map, contract); }

    /** Shows the entry price, reward and deadline before purchasing. */
    @Override
    public String menuDescription(Actor actor) {
        return "Accept " + contract.title() + " (fee " + contract.entryFee() +
                " coins, reward " + contract.coinReward() + " coins + " +
                contract.experienceReward() + " XP, " + contract.turnLimit() + " turns)";
    }
}
