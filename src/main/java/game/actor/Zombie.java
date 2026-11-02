package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.state.AttackingState;
import game.state.CreatureState;
import game.state.FleeingState;
import game.state.HealingState;
import game.state.WanderingState;


public class Zombie extends HostileCreature {
    private static final int MAX_HP = 100;
    private static final int ATTACK_DAMAGE = 20;
    private static final int HIT_CHANCE = 60;
    private static final double ATTACK_DAMAGE_BOOST = 1.10;
    private static final double HIT_CHANCE_BOOST = 1.15;

    /** Current combat values; restored to their base values at the next turn. */
    private int currentDamage = ATTACK_DAMAGE;
    private int currentHitChance = HIT_CHANCE;

    private final CreatureState wanderingState = new WanderingState();
    private final CreatureState attackingState = new AttackingState();
    private final CreatureState fleeingState = new FleeingState();
    private final CreatureState healingState = new HealingState();

    private CreatureState currentState;
    private boolean stateEntered;


    public Zombie() {
        super("Zombie", 'Z', MAX_HP);
        this.currentState = wanderingState;
    }


    public CreatureState getWanderingState() {
        return wanderingState;
    }


    public CreatureState getAttackingState() {
        return attackingState;
    }


    public CreatureState getFleeingState() {
        return fleeingState;
    }


    public CreatureState getHealingState() {
        return healingState;
    }


    @Override
    public int damage() {
        return currentDamage;
    }


    @Override
    public int hitChance() {
        return currentHitChance;
    }

    /**
     * Applies the temporary combat increase when entering the attacking state.
     * The values are reset at the beginning of the Zombie's next turn.
     */
    public void boostAttackForCurrentTurn() {
        currentDamage = (int) Math.round(ATTACK_DAMAGE * ATTACK_DAMAGE_BOOST);
        currentHitChance = (int) Math.round(HIT_CHANCE * HIT_CHANCE_BOOST);
    }

    /** Restores the Zombie's normal combat values. */
    private void resetAttackStats() {
        currentDamage = ATTACK_DAMAGE;
        currentHitChance = HIT_CHANCE;
    }


    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // An attacking-state entry bonus lasts for this turn only.
        resetAttackStats();

        if (!isConscious()) {
            unconscious(map);
            return new DoNothingAction();
        }

        CreatureState next = currentState.nextState(this, map);
        if (next != currentState) {
            System.out.println(
                    "Zombie: "
                            + currentState.getClass().getSimpleName()
                            + " -> "
                            + next.getClass().getSimpleName()
            );

            currentState = next;
            currentState.onEnter(this, map);
            stateEntered = true;
        } else if (!stateEntered) {
            // Run the entry effect for the initial state once as well.
            currentState.onEnter(this, map);
            stateEntered = true;
        }

        System.out.println(
                "Current state: "
                        + currentState.getClass().getSimpleName()
        );

        Action action = currentState.getAction(this, map);
        return action == null ? new DoNothingAction() : action;
    }
}
