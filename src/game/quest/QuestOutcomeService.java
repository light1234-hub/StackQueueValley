package game.quest;

import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actor.Farmer;
import game.actor.FinanceStatistics;

import java.util.Objects;

/**
 * Applies quest rewards and persistent farm consequences.
 * QuestManager delegates outcome handling here so quest coordination remains focused
 * on lifecycle, objectives, deadlines, and travel.
 */
public class QuestOutcomeService {
    private final Farmer farmer;
    private final QuestWorldContext world;

    /**
     * Creates an outcome service for the Farmer and persistent farm.
     *
     * @param farmer Farmer receiving successful-quest rewards
     * @param world controlled access to the persistent farm
     * @throws NullPointerException if a dependency is null
     */
    public QuestOutcomeService(Farmer farmer, QuestWorldContext world) {
        this.farmer = Objects.requireNonNull(farmer, "farmer must not be null");
        this.world = Objects.requireNonNull(world, "world must not be null");
    }

    /**
     * Applies either the success reward and consequence or the failure consequence.
     *
     * @param quest completed quest definition
     * @param successful whether every objective was completed before the deadline
     * @return message describing the rewards and persistent effects
     */
    public String apply(QuestDefinition quest, boolean successful) {
        Objects.requireNonNull(quest, "quest must not be null");
        if (successful) {
            farmer.gainExp(quest.experienceReward());
            farmer.modifyStatistic(FinanceStatistics.COINS, StatisticOperations.INCREASE,
                    quest.coinReward());
            return " Earned " + quest.experienceReward() + " XP and "
                    + quest.coinReward() + " coins. "
                    + quest.consequence().onSuccess(world);
        }
        return " No XP or coins awarded. " + quest.consequence().onFailure(world);
    }
}
