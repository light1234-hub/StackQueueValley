package game.effect;

import edu.monash.fit2099.engine.statistics.BaseStatistic;

/**
 * A health statistic that blocks damage while a {@link ReflectStatus} is active
 * and hands the blocked amount to that status. Once the status expires it behaves
 * exactly like a normal BaseStatistic. It replaces the actor's HEALTH statistic
 * through the public addNewStatistic(...), so no existing class is modified.
 */
public class ReflectingHealth extends BaseStatistic {
    private ReflectStatus status;

    /**
     * @param maximum the actor's maximum health
     * @param current the actor's current health
     */
    public ReflectingHealth(int maximum, int current) {
        super(maximum);
        update(current);
    }

    void setStatus(ReflectStatus status) {
        this.status = status;
    }

    @Override
    public void decrease(Integer points) {
        if (status != null && status.isStatusActive()) {
            status.reflect(points);
            return;
        }
        super.decrease(points);
    }
}