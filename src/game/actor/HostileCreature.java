package game.actor;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviour.WanderBehaviour;
import game.farmeraction.SwordAttackAction;
import game.farmeraction.CreatureAttackAction;
import game.item.Sword;

/** Shared wandering and combat behavior for mine enemies. */
public abstract class HostileCreature extends Actor {
    private final WanderBehaviour wander = new WanderBehaviour();

    protected HostileCreature(String name, char glyph, int hp) {
        super(name, glyph, hp);
    }

    protected abstract int damage();
    protected abstract int hitChance();
    public void onAttacked(Location location, boolean killed) { }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!isConscious()) {
            unconscious(map);
            return new edu.monash.fit2099.engine.actions.DoNothingAction();
        }
        Location here = map.locationOf(this);
        for (edu.monash.fit2099.engine.positions.Exit exit : here.getExits()) {
            Location next = exit.getDestination();
            if (next.containsAnActor() && next.getActor() instanceof Farmer)
                return new CreatureAttackAction((Farmer) next.getActor(), exit.getName(), damage(), hitChance());
        }
        Action action = wander.operate(this, here);
        return action == null ? new edu.monash.fit2099.engine.actions.DoNothingAction() : action;
    }

    @Override
    public ActionList allowableActions(Actor other, String direction, GameMap map) {
        ActionList actions = new ActionList();
        if (other instanceof Farmer) {
            for (edu.monash.fit2099.engine.items.Item item : other.getInventory().getItems()) {
                if (item instanceof Sword) actions.add(new SwordAttackAction(this, direction, (Sword) item));
            }
        }
        return actions;
    }
}
