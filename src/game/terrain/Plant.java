package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ability.GroundAbility;
import game.ability.TerrainAbility;
import game.blueprint.Harvestable;
import game.blueprint.Waterable;
import game.farmeraction.HarvestAction;
import game.farmeraction.WaterAction;
import game.item.CauliflowerItem;
import game.item.MelonItem;
import game.item.ParsnipItem;
import game.item.StrawberryItem;
import game.item.TomatoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** A plant whose species and life stage are represented as state. */
public final class Plant extends Ground implements Harvestable, Waterable {
    /** Stages through which a plant can progress. */
    public enum Stage { SEEDLING, SPROUT, CROP }

    private final PlantGrowth.Species species;
    private final Stage stage;
    private final Random random = new Random();
    private int age;
    private int timeWatered;
    private boolean skipNextTick;

    Plant(PlantGrowth.Species species, Stage stage) {
        super(glyph(species, stage), displayName(species, stage));
        this.species = species;
        this.stage = stage;
    }

    /** @return species represented by this plant */
    public PlantGrowth.Species species() { return species; }

    /** @return current life stage */
    public Stage stage() { return stage; }

    /** @return number of world turns this plant has aged */
    public int age() { return age; }

    @Override
    public void tick(Location location) {
        if (skipNextTick) {
            skipNextTick = false;
            return;
        }
        age++;
        String result = takeTurn(location);
        if (result != null) {
            System.out.println(toString() + " at (" + location.x() + ", " + location.y()
                    + ") on " + location.map() + " " + result);
        }
    }

    private String takeTurn(Location location) {
        if (species == PlantGrowth.Species.STRAWBERRY && stage == Stage.SEEDLING)
            return grow(location, 5, 0.50, Stage.SPROUT) ? "grows into a Strawberry Sprout." : null;
        if (species == PlantGrowth.Species.STRAWBERRY && stage == Stage.SPROUT) {
            if (grow(location, 10, 0.50, Stage.CROP)) return "grows into a Strawberry Crop.";
            Location child = spread(location, PlantGrowth.seedling(PlantGrowth.Species.STRAWBERRY), 5);
            return child == null ? null : spreadMessage("Strawberry Seedling", child);
        }
        if (species == PlantGrowth.Species.CAULIFLOWER && stage == Stage.SPROUT)
            return grow(location, 15, 0.25, Stage.CROP) ? "grows into a Cauliflower Crop." : null;
        if (species == PlantGrowth.Species.MELON && stage == Stage.SPROUT) {
            if (grow(location, 15, 0.25, Stage.CROP)) return "grows into a Melon Crop.";
            Location child = spread(location, PlantGrowth.sprout(PlantGrowth.Species.MELON), 10);
            return child == null ? null : spreadMessage("Melon Sprout", child);
        }
        if (stage != Stage.CROP) return null;

        switch (species) {
            case STRAWBERRY:
                return due(5) ? produce(location, "Strawberry", StrawberryItem::new) : null;
            case TOMATO:
                if (due(7)) return produce(location, "Tomato", TomatoItem::new);
                if (due(20) && random.nextDouble() < 0.10) {
                    Location tomatoChild = spread(location, PlantGrowth.crop(PlantGrowth.Species.TOMATO), 1);
                    if (tomatoChild != null) return spreadMessage("Tomato Crop", tomatoChild);
                }
                return null;
            case PARSNIP:
                return due(15) ? produce(location, "Parsnip", ParsnipItem::new) : null;
            case MELON:
                Location melonChild = spread(location, PlantGrowth.sprout(PlantGrowth.Species.MELON), 20);
                return melonChild == null ? null : spreadMessage("Melon Sprout", melonChild);
            case CAULIFLOWER:
            default:
                return null;
        }
    }

    private boolean grow(Location location, int interval, double probability, Stage nextStage) {
        if (!due(interval) || random.nextDouble() >= probability) return false;
        location.setGround(PlantGrowth.create(species, nextStage));
        return true;
    }

    private boolean due(int interval) { return age % interval == 0; }

    private Location spread(Location location, Plant child, int interval) {
        if (!due(interval)) return null;
        List<Location> soil = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround().hasAbility(GroundAbility.FERTILE)
                    && adjacent.getGround().getClass() != Plant.class) soil.add(adjacent);
        }
        if (soil.isEmpty()) return null;
        Location target = soil.get(random.nextInt(soil.size()));
        if (comesAfterInMapTickOrder(location, target)) child.skipNextTick = true;
        target.setGround(child);
        return target;
    }

    private boolean comesAfterInMapTickOrder(Location source, Location target) {
        return target.y() > source.y() || (target.y() == source.y() && target.x() > source.x());
    }

    private Location randomAdjacent(Location location) {
        List<Exit> exits = location.getExits();
        return exits.isEmpty() ? null : exits.get(random.nextInt(exits.size())).getDestination();
    }

    private String produce(Location location, String itemName,
                           java.util.function.Supplier<edu.monash.fit2099.engine.items.Item> item) {
        Location target = randomAdjacent(location);
        if (target == null) return null;
        target.addItem(item.get());
        return "drops a " + itemName + " at (" + target.x() + ", " + target.y() + ") on " + target.map() + ".";
    }

    private String spreadMessage(String childName, Location target) {
        return "spreads, and a " + childName + " takes root at (" + target.x() + ", " + target.y()
                + ") on " + target.map() + ".";
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (stage == Stage.CROP) {
            actions.add(new HarvestAction(this, location));
            if (species == PlantGrowth.Species.STRAWBERRY || species == PlantGrowth.Species.PARSNIP
                    || species == PlantGrowth.Species.MELON || species == PlantGrowth.Species.CAULIFLOWER)
                actions.add(new WaterAction(this, location));
        }
        return actions;
    }

    @Override
    public boolean canHarvest() { return species != PlantGrowth.Species.CAULIFLOWER || timeWatered >= 5; }

    @Override
    public int staminaCost() {
        switch (species) {
            case STRAWBERRY: return 1;
            case TOMATO: return 2;
            case PARSNIP: return 1;
            case MELON: return 5;
            case CAULIFLOWER: return 3;
            default: throw new IllegalStateException("Unsupported plant species: " + species);
        }
    }

    @Override
    public String harvest(Actor actor, Location location) {
        switch (species) {
            case STRAWBERRY:
                actor.getInventory().add(new StrawberryItem());
                actor.getInventory().add(new StrawberryItem());
                if (random.nextDouble() < Math.max(0, 50.0 - timeWatered) / 100.0) {
                    location.setGround(new Dirt());
                    return "Harvested two Strawberries, but the crop was destroyed!";
                }
                return "Harvested Two Strawberry!!";
            case TOMATO:
                location.setGround(new Dirt());
                actor.getInventory().add(new TomatoItem());
                actor.getInventory().add(new TomatoItem());
                actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 1);
                return "Harvested Tomato!!";
            case PARSNIP:
                location.setGround(new Dirt());
                actor.getInventory().add(new ParsnipItem());
                if (random.nextBoolean()) {
                    Location adjacent = randomAdjacent(location);
                    if (adjacent != null) adjacent.addItem(new ParsnipItem());
                }
                return "Harvested parsnip!!";
            case MELON:
                location.setGround(new Dirt());
                if (random.nextBoolean()) {
                    int damage = 20 - timeWatered;
                    if (damage > 0) {
                        if (location.containsAnActor()) location.getActor().hurt(damage);
                        for (Exit exit : location.getExits()) {
                            Location adjacent = exit.getDestination();
                            if (adjacent.containsAnActor()) adjacent.getActor().hurt(damage);
                        }
                    }
                    return "The Melon exploded, damaging all nearby actors!";
                }
                actor.getInventory().add(new MelonItem());
                return "Melon has been harvested and added into inventory";
            case CAULIFLOWER:
                location.setGround(new Dirt());
                if (random.nextBoolean()) {
                    actor.getInventory().add(new CauliflowerItem());
                    return "Harvested the Cauliflower successfully!";
                }
                return "Harvested the Cauliflower, but it yielded nothing.";
            default:
                throw new IllegalStateException("Unsupported plant species: " + species);
        }
    }

    @Override
    public int waterStamina() {
        return species == PlantGrowth.Species.CAULIFLOWER ? 2 : 1;
    }

    @Override
    public String water(Actor actor, Location location) {
        timeWatered++;
        switch (species) {
            case STRAWBERRY:
                for (int i = 0; i < 2; i++) {
                    Location target = randomAdjacent(location);
                    if (target != null) target.addItem(new StrawberryItem());
                }
                return "Watered Strawberry and added two Strawberry.";
            case PARSNIP:
                actor.heal(20);
                double destructionChance = (10.0 + timeWatered) / 100.0;
                if (random.nextDouble() < destructionChance) {
                    location.setGround(new Dirt());
                    return "Watered Parsnip, but the crop was destroyed";
                }
                return "Watered Parsnip and healed 20 HP!";
            case MELON:
                if (random.nextBoolean()) {
                    actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 1);
                    return "Watered Melon and MAX SP increase by 1 .";
                }
                return "Watered Melon.";
            case CAULIFLOWER:
                boolean puddleTriggered = random.nextBoolean();
                if (puddleTriggered) {
                    for (Exit exit : location.getExits()) {
                        Location plot = exit.getDestination();
                        if (plot.getGround().hasAbility(TerrainAbility.CAN_BE_PUDDLE)) plot.setGround(new Puddle());
                    }
                    return "Watered Cauliflower and adjacent ground turned into puddles!";
                }
                return "Watered Cauliflower";
            default:
                return "This crop cannot be watered.";
        }
    }

    private static char glyph(PlantGrowth.Species species, Stage stage) {
        switch (species) {
            case STRAWBERRY: return stage == Stage.SEEDLING ? '\'' : stage == Stage.SPROUT ? 'v' : 'S';
            case TOMATO: return 'T';
            case PARSNIP: return 'P';
            case MELON: return stage == Stage.SPROUT ? 't' : 'Q';
            case CAULIFLOWER: return stage == Stage.SPROUT ? 'y' : 'Y';
            default: throw new IllegalStateException("Unsupported plant species: " + species);
        }
    }

    private static String displayName(PlantGrowth.Species species, Stage stage) {
        String speciesName = species.name().charAt(0) + species.name().substring(1).toLowerCase();
        String stageName = stage == Stage.SEEDLING ? "Seedling" : stage == Stage.SPROUT ? "Sprout" : "Crop";
        return speciesName + " " + stageName;
    }

    @Override
    public String toString() { return displayName(species, stage); }
}
