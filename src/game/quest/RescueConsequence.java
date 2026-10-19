package game.quest;

import game.actor.Chicken;
import game.actor.Slime;
import game.item.Egg;
import game.item.StoneItem;
import game.terrain.Rock;
import game.terrain.Soil;


public class RescueConsequence implements QuestConsequence {

    @Override
    public String onSuccess(QuestWorldContext world) {
        world.changeGround(0, 3, new Soil());
        world.spawnNearby(7, 5, new Chicken());
        world.placeItem(7, 4, new Egg());
        return "A rescued chicken returned to the farm and left an egg.";
    }


    @Override
    public String onFailure(QuestWorldContext world) {
        world.changeGround(0, 3, new Rock());
        world.spawnNearby(7, 5, new Slime());
        world.placeItem(1, 4, new StoneItem());
        return "The rescue failed; a rockfall and hostile creature reached the farm.";
    }
}
