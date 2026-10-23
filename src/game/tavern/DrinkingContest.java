package game.tavern;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.FinanceStatistics;
import game.quest.QuestDefinition;
import game.quest.QuestManager;

import java.util.List;
import java.util.Random;

/**
 * Runs a three-round drinking wager without owning quest setup or rewards.
 * One Farmer competes against the bartender. A win grants one weighted-random
 * free contract; a tie, blackout, loss or walk-away grants none.
 *
 * <p>A completed attempt causes a five-turn cooldown before another wager may start.</p>
 */
public class DrinkingContest {
    private static final int ROUNDS = 3;
    private static final int LOST_STAMINA = 4;
    private static final int COOLDOWN_TURNS = 5;

    private final QuestManager quests;
    private final Random random;
    private Actor contender;
    private GameMap tavernMap;
    private int round;
    private int farmerCups;
    private int bartenderCups;
    private int cooldown;

    /** @param quests manager responsible for starting the random prize quest */
    public DrinkingContest(QuestManager quests) {
        this(quests, new Random());
    }

    /**
     * Constructor with injectable randomness for deterministic unit tests.
     *
     * @param quests manager responsible for contracts
     * @param random random number generator for drinks, blackouts and prizes
     */
    public DrinkingContest(QuestManager quests, Random random) {
        if (quests == null || random == null) {
            throw new IllegalArgumentException("Quest manager and random generator are required");
        }
        this.quests = quests;
        this.random = random;
    }

    /** @return whether a drinking contest is currently underway */
    public boolean inProgress() { return contender != null; }

    /** @return number of completed drinking rounds */
    public int completedRounds() { return round; }

    /** @return remaining bartender turns before another attempt is allowed */
    public int cooldownTurns() { return cooldown; }

    /**
     * A wager is offered only when the Farmer cannot afford at least one
     * listed contract and is eligible to talk to the bartender.
     *
     * @param actor the contestant
     * @param map the current Tavern map
     * @return true if the wager may be started
     */
    public boolean canChallenge(Actor actor, GameMap map) {
        if (inProgress() || cooldown > 0 || quests.hasActiveQuest()
                || !quests.canInteractAtBar(actor, map)) return false;
        int coins = actor.getStatistic(FinanceStatistics.COINS);
        for (QuestDefinition contract : quests.contracts()) {
            if (coins < contract.entryFee()) return true;
        }
        return false;
    }

    /**
     * Begins the wager; does not charge coins or move the Farmer.
     *
     * @param actor the Farmer challenging the bartender
     * @param map the current Tavern map
     * @return instructions or an eligibility failure message
     */
    public String start(Actor actor, GameMap map) {
        if (!canChallenge(actor, map)) {
            return "The drinking wager is unavailable. You must be short of coins for a contract, "
                    + "beside the bartender, and off cooldown.";
        }
        contender = actor;
        tavernMap = map;
        round = 0;
        farmerCups = 0;
        bartenderCups = 0;
        return "Bartender: Outdrink me in three rounds and I'll waive a random contract's "
                + "entry fee! Sip scores 1 cup safely; Chug scores 2 but has a 20% blackout risk. "
                + "A tie is a loss. Walking away forfeits the challenge.";
    }

    /**
     * Plays one round and finishes the wager after three rounds or a blackout.
     * The last successful round assigns a randomly weighted contract without
     * charging the normal entry fee.
     *
     * @param actor the contender
     * @param map the map where the drinking action occurs
     * @param choice the contender's drinking strategy
     * @return the updated score, outcome or a stale-action error
     */
    public String drink(Actor actor, GameMap map, DrinkChoice choice) {
        if (choice == null || actor != contender || map != tavernMap
                || !quests.canInteractAtBar(actor, map) || quests.hasActiveQuest()) {
            return "You must be in an active drinking contest beside the bartender.";
        }
        round++;
        farmerCups += choice.cups();
        bartenderCups += 1 + random.nextInt(2);

        String score = "Round " + round + "/" + ROUNDS + ": Farmer " + farmerCups
                + " cups, bartender " + bartenderCups + " cups. ";

        if (choice.blackoutChance() > 0 && random.nextDouble() < choice.blackoutChance()) {
            return score + endLoss("You blacked out!");
        }
        if (round < ROUNDS) {
            return score + "Choose Sip or Chug again.";
        }
        if (farmerCups <= bartenderCups) {
            return score + endLoss("The bartender outdrank you (ties go to the bartender). ");
        }

        // Roll the prize only after a verified win. No additional contract fee.
        List<QuestDefinition> contracts = quests.contracts();
        QuestDefinition prize = pickPrize(contracts);
        String start = quests.acceptWagerPrize(actor, map, prize);
        if (!quests.hasActiveQuest()) {
            // Do not consume the cooldown for an engine/map preparation failure.
            contender = null;
            tavernMap = null;
            round = 0;
            farmerCups = 0;
            bartenderCups = 0;
            return score + "You won, but the free contract could not start: " + start
                    + " No cooldown was charged.";
        }
        finishAttempt();
        return score + "You won! Random free contract: " + prize.title() + ". " + start;
    }

    /**
     * Advances the cooldown and forfeits an active contest if the contender
     * is no longer beside the bartender. Invoked from the bartender's turn.
     *
     * @return an optional message about a forfeited contest
     */
    public String advanceTurn() {
        if (cooldown > 0) cooldown--;
        if (contender != null && !quests.canInteractAtBar(contender, tavernMap)) {
            return endLoss("You left the bar and forfeited the drinking wager.");
        }
        return null;
    }

    /** Applies stamina loss and cooldown exactly once for any failed wager. */
    private String endLoss(String explanation) {
        contender.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE,
                LOST_STAMINA);
        finishAttempt();
        return explanation + " No quest awarded. Lost " + LOST_STAMINA + " stamina.";
    }

    /** Clears contest state and prevents immediate repeat attempts. */
    private void finishAttempt() {
        contender = null;
        tavernMap = null;
        round = 0;
        farmerCups = 0;
        bartenderCups = 0;
        cooldown = COOLDOWN_TURNS;
    }

    /**
     * Selects among the three current contract tiers with weights 50/35/15.
     * A non-three-contract configuration falls back to uniform selection.
     */
    private QuestDefinition pickPrize(List<QuestDefinition> contracts) {
        if (contracts.size() != 3) {
            return contracts.get(random.nextInt(contracts.size()));
        }
        int roll = random.nextInt(100);
        if (roll < 50) return contracts.get(0);
        if (roll < 85) return contracts.get(1);
        return contracts.get(2);
    }
}