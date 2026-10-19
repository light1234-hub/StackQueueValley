package game.quest;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public class QuestObjectiveGround extends Ground {
    private final QuestManager manager;
    private final Location location;
    private final String objectiveName;


    public QuestObjectiveGround(QuestManager manager, Location location, String objectiveName) {
        super('!', objectiveName);
        this.manager = manager;
        this.location = location;
        this.objectiveName = objectiveName;
    }

    @Override
    public boolean canActorEnter(Actor actor) { return false; }

    @Override
    public ActionList allowableActions(Actor actor, Location here, String direction) {
        ActionList actions = super.allowableActions(actor, here, direction);
        if (!direction.isEmpty() && manager.canInteract(actor, this)) {
            actions.add(new ActivateObjectiveAction(manager, this, objectiveName));
        }
        return actions;
    }

    public Location location() { return location; }
}
