package game.tavern;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import game.actor.NonPlayerCharacter;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.ability.TavernAbility;
import game.actor.Farmer;
import game.quest.AcceptContractAction;
import game.quest.QuestDefinition;
import game.quest.QuestManager;

/**
 * Stationary bartender offering conversation, paid contracts, and a drinking
 * wager. Dialogue, quests, and contest rules are delegated to separate classes.
 */
public class ContractBartender extends NonPlayerCharacter {
    private final QuestManager manager;
    private final DrinkingContest contest;
    private final BartenderDialogue dialogue;
    private final Farmer farmer;

    /**
     * Creates a bartender connected to a contract service.
     *
     * @param manager the Tavern's contract manager
     */
    public ContractBartender(QuestManager manager) {
        this(manager, null);
    }

    /**
     * Creates a bartender that can restrict the specified Farmer's menu.
     *
     * @param manager contract manager that owns the available quests
     * @param farmer player whose regular actions are hidden during dialogue
     */
    public ContractBartender(QuestManager manager, Farmer farmer) {
        super("Bartender", 'B', 100);
        if (manager == null) throw new IllegalArgumentException("Manager cannot be null");
        this.manager = manager;
        this.farmer = farmer;
        this.contest = new DrinkingContest(manager);
        this.dialogue = new BartenderDialogue(manager, contest);
    }

    /**
     * Advances contest cooldown and ends conversations interrupted by movement.
     *
     * @param actions the bartender's possible actions
     * @param lastAction the action previously taken
     * @param map the current Tavern map
     * @param display the display for forfeiture messages
     * @return a do-nothing action because the bartender remains at the bar
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        dialogue.closeIfInterrupted();
        String message = contest.advanceTurn();
        if (message != null) display.println(message);
        return new DoNothingAction();
    }

    /**
     * Builds a context-sensitive interaction menu. Players first talk to the
     * bartender, then choose contracts or wager dialogue. During an active
     * drinking contest, only Sip and Chug are offered.
     *
     * @param otherActor the actor interacting with the bartender
     * @param direction direction from that actor to the bartender
     * @param map the Tavern map
     * @return the available actions appropriate to the current dialogue stage
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = super.allowableActions(otherActor, direction, map);
        if (!otherActor.hasAbility(TavernAbility.CAN_USE_TAVERN)
                || direction.isEmpty() || !manager.canInteractAtBar(otherActor, map)
                || manager.hasActiveQuest()) {
            return actions;
        }

        // Build a separate set of bartender choices. Only this set appears
        // on the Farmer's screen while a conversation or contest is active.
        ActionList dialogueActions = new ActionList();
        if (contest.inProgress()) {
            dialogueActions.add(new DrinkRoundAction(contest, DrinkChoice.SIP));
            dialogueActions.add(new DrinkRoundAction(contest, DrinkChoice.CHUG));
            actions.add(dialogueActions);
            if (farmer != null && otherActor == farmer) {
                farmer.startConversation(dialogueActions, map);
            }
            return actions;
        }

        BartenderDialogue.Stage stage = dialogue.stageFor(otherActor, map);
        switch (stage) {
            case CLOSED -> dialogueActions.add(new BartenderDialogueAction(
                    dialogue, BartenderDialogue.Topic.GREET));
            case GREETING -> {
                dialogueActions.add(new BartenderDialogueAction(
                        dialogue, BartenderDialogue.Topic.ASK_CONTRACTS));
                if (contest.canChallenge(otherActor, map)) {
                    dialogueActions.add(new BartenderDialogueAction(
                            dialogue, BartenderDialogue.Topic.ASK_WAGER));
                }
                dialogueActions.add(new BartenderDialogueAction(
                        dialogue, BartenderDialogue.Topic.SAY_GOODBYE));
            }
            case CONTRACTS -> {
                for (QuestDefinition contract : manager.contracts()) {
                    dialogueActions.add(new AcceptContractAction(manager, contract));
                }
                dialogueActions.add(new BartenderDialogueAction(
                        dialogue, BartenderDialogue.Topic.GO_BACK));
            }
            case WAGER -> {
                if (contest.canChallenge(otherActor, map)) {
                    dialogueActions.add(new StartDrinkingContestAction(contest, dialogue));
                }
                dialogueActions.add(new BartenderDialogueAction(
                        dialogue, BartenderDialogue.Topic.GO_BACK));
            }
        }

        actions.add(dialogueActions);
        if (farmer != null && otherActor == farmer) {
            if (stage == BartenderDialogue.Stage.CLOSED) {
                farmer.endConversation();
            } else {
                farmer.startConversation(dialogueActions, map);
            }
        }
        return actions;
    }
}
