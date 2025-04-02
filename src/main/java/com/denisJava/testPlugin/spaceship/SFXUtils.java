package com.denisJava.testPlugin.spaceship;

import com.denisJava.testPlugin.TestPlugin;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import static com.denisJava.testPlugin.TestPlugin.SCHEDULER;

public class SFXUtils {
    public static Vector axisToVectorPositive(Axis axis) {
        switch (axis) {
            case X -> {
                return new Vector(1, 0, 0);
            }
            case Y -> {
                return new Vector(0, 1, 0);
            }
            case Z -> {
                return new Vector(0, 0, 1);
            }
        }
        return new Vector();
    }
    public static void setBlock(World world, String block, Vector vector) {
        world.setBlockData(vector.toLocation(world), Bukkit.getServer().createBlockData(block));
        world.getBlockAt(vector.toLocation(world)).tick();
    }
    public static void door(World world, Axis axis, boolean open, Vector pos) { // pos: -62 121 141
        String upperBlock = open ? "minecraft:air" : "minecraft:polished_tuff_wall";
        String downBlock = open ? "minecraft:air" : "minecraft:polished_blackstone_wall";
        long[] timings = open ? new long[]{20, 40, 60} : new long[]{60, 40, 20};
        Vector axisV = axisToVectorPositive(axis).multiply(-1);

        if (!open) {
            if (axis == Axis.Z) {
                upperBlock += "[east=none,north=tall,south=tall,up=false,waterlogged=false,west=none]";
                downBlock += "[east=none,north=tall,south=tall,up=false,waterlogged=false,west=none]";
            } else {
                upperBlock += "[east=tall,north=none,south=none,up=false,waterlogged=false,west=tall]";
                downBlock += "[east=tall,north=none,south=none,up=false,waterlogged=false,west=tall]";
            }
        }

        String finalDownBlock = downBlock;
        SCHEDULER.runTaskLater(TestPlugin.plugin, () -> {
            Vector v = pos.clone();
            setBlock(world, finalDownBlock, v);
            setBlock(world, finalDownBlock, v.add(axisV));
            world.playSound(v.toLocation(world), Sound.BLOCK_VAULT_REJECT_REWARDED_PLAYER, 1, 0);
            setBlock(world, finalDownBlock, v.add(axisV));
        }, timings[0]);
        String finalUpperBlock = upperBlock;
        SCHEDULER.runTaskLater(TestPlugin.plugin, () -> {
            Vector v = pos.clone().add(new Vector(0, 1, 0));
            setBlock(world, finalUpperBlock, v);
            setBlock(world, finalUpperBlock, v.add(axisV));
            world.playSound(v.toLocation(world), Sound.BLOCK_VAULT_REJECT_REWARDED_PLAYER, 1, 0);
            setBlock(world, finalUpperBlock, v.add(axisV));
        }, timings[1]);
        String finalUpperBlock1 = upperBlock;
        SCHEDULER.runTaskLater(TestPlugin.plugin, () -> {
            Vector v = pos.clone().add(new Vector(0, 2, 0));
            setBlock(world, finalUpperBlock1, v);
            setBlock(world, finalUpperBlock1, v.add(axisV));
            world.playSound(v.toLocation(world), Sound.BLOCK_VAULT_REJECT_REWARDED_PLAYER, 1, 0);
            setBlock(world, finalUpperBlock1, v.add(axisV));
        }, timings[2]);
    }
}
