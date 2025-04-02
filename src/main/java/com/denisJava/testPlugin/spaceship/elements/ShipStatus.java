package com.denisJava.testPlugin.spaceship.elements;

import com.denisJava.testPlugin.TestPlugin;
import com.denisJava.testPlugin.spaceship.ShipNode;
import com.denisJava.testPlugin.spaceship.ShipNodeStatus;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

import static com.denisJava.testPlugin.TestPlugin.plugin;

public class ShipStatus {
    private int energy;
    private int oxygen;
    private int water;

    private World world;

    private ShipNode[] nodes;
    private ShipNodeStatus[] nodeStatuses;

    public ShipStatus(World world) {
        energy = 100000;
        oxygen = 50000;
        water = 15000;

        this.world = world;

        nodes = new ShipNode[]{
                new ShipPump("Помпа #1", new Location(world, -57, 121, 138)),

                new ShipGenerator("Генератор #1", new Location(world, -41, 122, 143), new Location(world, -40, 121, 142)).repair(),
        };
        nodeStatuses = new ShipNodeStatus[]{
                ShipNodeStatus.OK, ShipNodeStatus.OK
        };

        for (int i = 0; i < nodes.length; i++) {
            ShipNode node = nodes[i];
            Sign block = (Sign) world.getBlockAt(node.signPos).getState();

            if (block.hasMetadata("nodeId")) {
                block.removeMetadata("nodeId", plugin);
            }
            block.setMetadata("nodeId", new FixedMetadataValue(plugin, i));
            block.setWaxed(true);
            block.update();
        }
    }

    public void updateLoop() {
        for (int i = 0; i < nodes.length; i++) {
            nodeStatuses[i] = nodes[i].fullUpdate(this, i, nodeStatuses[i]);
        }
    }

    public boolean consumeEnergy(int amount) {
        if (energy >= amount) {energy -= amount; return true;}
        return false;
    }
    public void produceEnergy(int amount) {
        energy += amount;
    }
    public int getEnergy() {
        return energy;
    }
    public boolean consumeOxygen(int amount) {
        if (oxygen >= amount) {oxygen -= amount; return true;}
        return false;
    }
    public void produceOxygen(int amount) {
        oxygen += amount;
    }
    public int getOxygen() {
        return oxygen;
    }
    public boolean consumeWater(int amount) {
        if (water >= amount) {water -= amount; return true;}
        return false;
    }
    public void produceWater(int amount) {
        water += amount;
    }
    public int getWater() {
        return water;
    }

    public ShipNode getNode(int i) {
        return nodes[i];
    }

    public World getWorld() {
        return world;
    }
}
