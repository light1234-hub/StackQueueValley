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

    private final CreatureState wanderingState = new WanderingState();
    private final CreatureState attackingState = new AttackingState();
    private final CreatureState fleeingState = new FleeingState();
    private final CreatureState healingState = new HealingState();

    private CreatureState currentState;


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
        return ATTACK_DAMAGE;
    }


    @Override
    public int hitChance() {
        return HIT_CHANCE;
    }


    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            unconscious(map);
            return new DoNothingAction();
        }

        CreatureState next = currentState.nextState(this, map);
        if (next != currentState) {
            currentState = next;
            currentState.onEnter(this, map);
        }

        Action action = currentState.getAction(this, map);
        return action == null ? new DoNothingAction() : action;
    }
}