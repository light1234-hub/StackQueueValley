package game.quest;

import game.actor.Slime;
import game.item.StrawberryItem;
import game.terrain.CorruptedDirt;
import game.terrain.Soil;

/** Spreads or reverses woodland corruption across the inherited farm. */
public class ForestConsequence implements QuestConsequence {
    /** Purifies two farm plots and produces a new harvest on the farm. */
    @Override
    public String onSuccess(QuestWorldContext world) {
        world.changeGround(0, 3, new Soil());
        world.changeGround(1, 5, new Soil());
        world.placeItem(0, 3, new StrawberryItem(false));
        return "Cleansed soil and a strawberry appeared on the farm.";
    }

    /** Infects farm soil and releases a hostile creature into the overworld. */
    @Override
    public String onFailure(QuestWorldContext world) {
        world.changeGround(0, 3, new CorruptedDirt());
        world.changeGround(1, 5, new CorruptedDirt());
        world.spawnNearby(7, 5, new Slime());
        return "Corruption reached the farm and a slime was unleashed.";
    }
}
