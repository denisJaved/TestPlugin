package com.denisJava.testPlugin.spaceship;

import org.bukkit.Material;

public class RepairIngredient {
    public Material item;
    public int amount;

    public RepairIngredient(int amount, Material item) {
        this.amount = amount;
        this.item = item;
    }
}
