package game.blueprint;

import edu.monash.fit2099.engine.capabilities.Status;

/** A ground status that supplies a coating while it remains active. */
public interface CoatingStatus extends Status, CoatingSource {
    /** A status source is usable only while the status is active. */
    @Override
    default boolean isAvailable() {
        return isStatusActive();
    }
}
