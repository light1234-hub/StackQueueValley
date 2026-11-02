package game.effect;

import edu.monash.fit2099.engine.capabilities.Status;

/** Permanent marker: the hostile creature no longer attacks the farmer. */
public class TamedStatus implements Status {
    @Override
    public boolean isStatusActive() {
        return true;
    }
}