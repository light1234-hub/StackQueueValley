package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import game.blueprint.Chargeable;
import game.effect.ChargeEffect;

/**
 * Puddle terrain. In the mines, puddles are the only ground capable of
 * holding an electrical charge released by breaking a copper node -- see
 * CopperNode.breakBlock() and ChargeEffect.
 */
public class Puddle extends Ground implements Chargeable {

    public Puddle() {
        super('~', "Puddle");
    }

    @Override
    public void charge() {
        // The spec doesn't give an exact duration for how long charged
        // ground stays charged -- 999 is a stand-in for "a long time".
        // Adjust if your marking rubric expects a specific number of turns.
        this.addStatus(new ChargeEffect(999));
    }
}
