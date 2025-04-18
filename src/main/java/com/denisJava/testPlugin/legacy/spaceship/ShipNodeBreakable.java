package com.denisJava.testPlugin.legacy.spaceship;

import com.denisJava.testPlugin.TestPlugin;
import com.denisJava.testPlugin.legacy.spaceship.elements.ShipStatus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

public abstract class ShipNodeBreakable extends ShipNode {
    public int durability;
    public Location repairPos;
    public ArrayList<RepairIngredient> repairIngredients;
    public ArrayList<RepairIngredient> currentRepair;

    public ShipNodeBreakable(String name, Location sign, ArrayList<RepairIngredient> repairIngredients, Location repairPos) {
        super(name, sign);
        this.repairIngredients = repairIngredients;
        this.repairPos = repairPos;
    }

    public abstract ShipNodeBreakable repair();

    @Override
    public ShipNodeStatus updateMixin(ShipStatus sh, int id, ShipNodeStatus status) {
        if (status == ShipNodeStatus.REPAIRING && durability > 0) {
            return ShipNodeStatus.OK;
        }
        if (status == ShipNodeStatus.BROKEN) {
            currentRepair = new ArrayList<>();

            for (RepairIngredient ingredient : repairIngredients) {
                currentRepair.add(new RepairIngredient(ingredient.amount, ingredient.item));
            }

            Block block = sh.getWorld().getBlockAt(repairPos);
            block.setType(Material.BARREL);
            Barrel bs = (Barrel) block.getState();
            bs.setMetadata("repairingNode", new FixedMetadataValue(TestPlugin.plugin, id));
            sh.getWorld().playSound(repairPos, Sound.BLOCK_ANVIL_BREAK, 1F, 0.6F);
            sh.getWorld().playSound(repairPos, Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1F, 0.6F);
            sh.getWorld().spawnParticle(Particle.EXPLOSION, signPos, 30);

            return ShipNodeStatus.REPAIRING;
        }

        return super.updateMixin(sh, id, status);
    }
}
