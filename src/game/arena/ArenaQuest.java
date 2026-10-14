package game.arena;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actor.Farmer;
import game.terrain.Dirt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages a reusable quest arena that can be configured for different quests.
 * Provides methods for placing terrain, spawning actors and adding items,
 * while tracking temporary objects for cleanup between quests.
 */
public final class ArenaQuest {
    private final GameMap map;
    private final Farmer farmer;
    private final List<Actor> spawnedActors = new ArrayList<>();
    private final List<SpawnedItem> spawnedItems = new ArrayList<>();

    /**
     * Stores an item and its original spawn location for later cleanup.
     *
     * @param location the location where the item was placed
     * @param item the spawned item
     */
    private record SpawnedItem(Location location, Item item){

    }

    /**
     * Creates a quest arena using an existing game map and Farmer.
     *
     * @param map the game map used as the quest arena
     * @param farmer the Farmer participating in quests
     * @throws NullPointerException if map or farmer is null
     */
    public ArenaQuest(GameMap map,Farmer farmer){
        this.map = Objects.requireNonNull(map);
        this.farmer = Objects.requireNonNull(farmer);
    }

    /**
     * Returns the game map associated with this arena.
     *
     * @return the arena's game map
     */
    public GameMap map(){
        return map;
    }

    /**
     * Checks whether the specified coordinates are within the arena.
     *
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @return true if both coordinates are within the map, otherwise false
     */
    public boolean inside(int x,int y){
        return map.getXRange().contains(x) && map.getYRange().contains(y);
    }

    /**
     * Places a ground object at the specified coordinates.
     * Placement fails if the location is invalid, the ground is null,
     * or an actor occupies the location.
     *
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @param ground the ground object to place
     * @return true if the ground was placed successfully, otherwise false
     */
    public boolean placeGround(int x, int y, Ground ground){
        Location loc = map.at(x,y);

        if(!inside(x,y) || ground == null){
            return false;
        }

        if(loc.containsAnActor()){
            return false;
        }
        loc.setGround(ground);
        return  true;
    }

    /**
     * Spawns an actor at the specified location and tracks it for cleanup.
     * Placement fails if the location is invalid, occupied, or cannot
     * be entered by the actor.
     *
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @param actor the actor to spawn
     * @return true if the actor was spawned successfully, otherwise false
     */
    public boolean spawnActor(int x, int y, Actor actor){
        Location tile = map.at(x,y);

        if(!inside(x,y) || actor == null){
            return false;
        }

        if(!tile.canActorEnter(actor) || tile.containsAnActor()){
            return false;
        }

        try{
            map.addActor(actor,tile);
            spawnedActors.add(actor);
            return true;
        } catch (GameEngineException exception){
            return false;
        }
    }

    /**
     * Places an item at the specified location and records it for cleanup.
     *
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     * @param item the item to place
     * @return true if the item was placed successfully, otherwise false
     */
    public boolean placeItem(int x, int y, Item item){
        Location tile = map.at(x,y);
        if(!inside(x,y) || item == null){
            return false;
        }
        tile.addItem(item);
        spawnedItems.add(new SpawnedItem(tile,item));
        return true;
    }

    /**
     * Resets the arena by removing tracked temporary actors and items,
     * then restoring its default walls and Dirt terrain.
     * Preserves the permanent exit at (1, 2) and locations occupied
     * by actors. The Farmer must leave the arena before it can be reset.
     *
     * @throws IllegalStateException if the Farmer is still inside the arena
     */
    public void reset(){
        if(map.contains(farmer)){
            throw new IllegalStateException("Cannot reset arena while the Farmer is inside");
        }
        for(Actor actor : spawnedActors){
            if(map.contains(actor)){
                map.removeActor(actor);
            }
        }
        spawnedActors.clear();
        for(SpawnedItem entry: spawnedItems){
            if(entry.location().getItems().contains(entry.item())){
                entry.location.removeItem(entry.item());
            }
        }
        spawnedActors.clear();
        for(int y: map.getYRange()){
            for(int x : map.getXRange()){
                Location tile = map.at(x,y);
                if(x == 1 && y == 2){
                    continue;
                }
                if(tile.containsAnActor()){
                    continue;
                }
                boolean wall = x == 0|| y == 0 || x == map.getXRange().max() || y == map.getYRange().max();
                tile.setGround(wall ? TavernWall() : new Dirt());
            }
        }
    }

}
