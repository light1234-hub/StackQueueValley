package game.quest;

import game.actor.CaveGhost;
import game.item.CopperItem;
import game.item.DiamondItem;
import game.terrain.CorruptedDirt;
import game.terrain.Geode;
import game.terrain.Soil;


public class RelicConsequence implements QuestConsequence {


    @Override
    public String onSuccess(QuestWorldContext world) {
        world.changeGround(0, 3, new Soil());
        world.placeItem(4, 4, new DiamondItem());
        world.placeItem(6, 4, new CopperItem());
        return "A diamond and copper appeared on the restored farm.";
    }


    @Override
    public String onFailure(QuestWorldContext world) {
        world.changeGround(0, 3, new Geode());
        world.changeGround(1, 5, new CorruptedDirt());
        world.spawnNearby(8, 4, new CaveGhost());
        return "A cave ghost escaped and cursed relic stones scarred the farm.";
    }
}
