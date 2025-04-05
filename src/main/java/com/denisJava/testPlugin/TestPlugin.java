package com.denisJava.testPlugin;

import com.denisJava.testPlugin.legacy.spaceship.GameLoop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.logging.Logger;

public class TestPlugin extends JavaPlugin implements Listener {
    public static final Random random = new Random();
    public static BukkitScheduler SCHEDULER;
    public static TestPlugin plugin;

    @Override
    public void onEnable() {
        GameLoop.instance = new GameLoop();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(GameLoop.instance, this);
        SCHEDULER = getServer().getScheduler();
        plugin = this;

        //noinspection UnstableApiUsage
        CommandRegistry.register(getLifecycleManager());
        getLogger().info("$ Test plugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("$ Test plugin disabled");
    }

    public static Vector lastParkourOffset = new Vector(0, 1, 0);
    public static Vector currentParkourBlock = new Vector();
    public static boolean iceBlocks = false;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        World world = event.getPlayer().getWorld();
        Location location = event.getPlayer().getLocation().toBlockLocation().add(0, -1, 0).toLocation(world);
        Block block = world.getBlockAt(location);
        if (location.toVector().equals(currentParkourBlock)) {
            block.setType(Material.EMERALD_BLOCK);
            if (iceBlocks) block.setType(Material.PACKED_ICE);
            world.setBlockData(location, block.getBlockData());

            Vector newOffset = new Vector(random.nextInt(0, 4) * (random.nextBoolean()?1:-1), 0, random.nextInt(0, 4) * (random.nextBoolean()?1:-1));
            if (newOffset.getX() == 0 && newOffset.getZ() == 0) newOffset = new Vector(0, 0, 2);
            if (newOffset.getX() == 1) newOffset.setX(2);
            if (newOffset.getZ() == 1) newOffset.setZ(2);
            if (newOffset.getX() == -1) newOffset.setX(-2);
            if (newOffset.getZ() == -1) newOffset.setZ(-2);

            Block next = world.getBlockAt(currentParkourBlock.clone().add(newOffset).toLocation(world));
            world.setBlockData(next.getLocation(), iceBlocks ? Material.BLUE_ICE.createBlockData() : Material.GOLD_BLOCK.createBlockData());
            currentParkourBlock = currentParkourBlock.clone().add(newOffset).toLocation(world).toVector();

            Block previous = world.getBlockAt(location.toBlockLocation().add(lastParkourOffset.multiply(-1)));
            previous.setType(Material.AIR);
            world.setBlockData(previous.getLocation(), previous.getBlockData());

            world.playSound(event.getPlayer(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, 0);
            world.spawnParticle(Particle.FIREWORK, event.getPlayer().getLocation(), 20);

            event.getPlayer().give(ItemStack.of(Material.DIAMOND));

            lastParkourOffset = newOffset;
        }
        if (!currentParkourBlock.equals(new Vector()) && currentParkourBlock.clone().add(lastParkourOffset).getY() - event.getPlayer().getY() >= 3) {
            world.playSound(event.getPlayer(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 0);
            event.getPlayer().getInventory().clear();
            event.getPlayer().teleport(currentParkourBlock.clone().add(lastParkourOffset.clone().multiply(-1)).add(new Vector(0.5, 1, 0.5)).toLocation(world));
            event.getPlayer().showTitle(Title.title(Component.text("Ты упал!").color(TextColor.color(255, 0, 0)), Component.text("Прыгай на ").append(Component.text("золотые блоки").color(TextColor.color(255, 255, 0)))));
        }
    }
    public static Logger logger() {
        return plugin.getLogger();
    }
}
