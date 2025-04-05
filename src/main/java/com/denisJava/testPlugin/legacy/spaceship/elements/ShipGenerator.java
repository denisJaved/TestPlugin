package com.denisJava.testPlugin.legacy.spaceship.elements;

import com.denisJava.testPlugin.legacy.spaceship.RepairIngredient;
import com.denisJava.testPlugin.legacy.spaceship.ShipNodeBreakable;
import com.denisJava.testPlugin.legacy.spaceship.ShipNodeStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShipGenerator extends ShipNodeBreakable {
    public ShipGenerator(String name, Location sign, Location repairPos) {
        super(name, sign, new ArrayList<>(List.of(new RepairIngredient[]{
                new RepairIngredient(4, Material.IRON_BLOCK),
                new RepairIngredient(16, Material.COPPER_INGOT),
                new RepairIngredient(1, Material.CAULDRON),
        })), repairPos);
    }

    @Override
    public ShipNodeBreakable repair() {
        durability = 10000;
        return this;
    }

    @Override
    public ShipNodeStatus update(ShipStatus sh, ShipNodeStatus status) {
        if (sh.consumeWater(100)) {
            sh.produceEnergy(100);
            durability -= 50;
            return ShipNodeStatus.OK;
        }
        return ShipNodeStatus.NOT_ENOUGH_RESOURCES;
    }

    @Override
    public @NotNull Component getFourthLine() {
        return Component.text(durability).color(TextColor.color(200, 50, 50));
    }
}
