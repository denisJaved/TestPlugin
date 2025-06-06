package com.denisJava.testPlugin.legacy.spaceship;

import com.denisJava.testPlugin.legacy.spaceship.elements.ShipStatus;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.denisJava.testPlugin.TestPlugin.*;

public class GameLoop implements Listener {
    public static GameLoop instance;

    private static boolean running = false;
    public static ShipStatus ship;

    public static void startLoop(World world) {
        if (running) return;
        ship = new ShipStatus(world);
        SCHEDULER.runTaskTimer(plugin, loop, 20, 10);
        running = true;
        logger().info("SpaceShip started");
    }

    public static void stopLoop() {
        running = false;
    }


    public static Scoreboard scoreboard;
    public static Objective objective;
    public static final Consumer<BukkitTask> loop = bukkitTask -> {
        if (!running) {
            logger().info("SpaceShip stopped");
            bukkitTask.cancel();
            return;
        }

        ship.updateLoop();

        // Update scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (scoreboard == null) scoreboard = manager.getMainScoreboard();
        if (objective == null) {
            objective = scoreboard.getObjective("info");
            if (objective != null) objective.unregister();
            objective = scoreboard.registerNewObjective("info", Criteria.DUMMY, Component.text("Информация"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        Score water = objective.getScore("Вода:");
        water.setScore(ship.getWater());
        Score energy = objective.getScore("Электричевство:");
        energy.setScore(ship.getEnergy());
        Score oxygen = objective.getScore("Воздух:");
        oxygen.setScore(ship.getOxygen());
    };

    @EventHandler
    public void onBlockInteraction(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (event.getClickedBlock().getType() == Material.BARREL) {
            List<MetadataValue> repairs = event.getClickedBlock().getState().getMetadata("repairingNode");
            if (!repairs.isEmpty()) {
                event.setCancelled(true);

                int id = repairs.getFirst().asInt();
                ShipNodeBreakable node = (ShipNodeBreakable) ship.getNode(id);

                Inventory inventory = Bukkit.createInventory(null, 9, Component.text("Ремонт: ").append(Component.text(node.name)));

                updateRepairScreen(node, inventory);

                player.openInventory(inventory);
                player.setMetadata("repair-ui", new FixedMetadataValue(plugin, id));
            }
        } else if (event.getClickedBlock().getType() == Material.BAMBOO_WALL_SIGN) {
            List<MetadataValue> nodes = event.getClickedBlock().getState().getMetadata("nodeId");
            if (!nodes.isEmpty()) {
                event.setCancelled(true);
                ShipNode node = ship.getNode(nodes.getFirst().asInt());
                node.shouldStop = !node.shouldStop;
            }
        }
    }

    private static void updateRepairScreen(ShipNodeBreakable node, Inventory inventory) {
        for (int i = 0; i < 8; i++) {
            if (i >= node.currentRepair.size()) {
                inventory.setItem(i, new ItemStack(Material.AIR));
                continue;
            }
            RepairIngredient ri = node.currentRepair.get(i);
            inventory.setItem(i, new ItemStack(ri.item).asQuantity(ri.amount));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.hasMetadata("repair-ui") && event.getClickedInventory() != null) {
            event.setCancelled(true);
            if (event.getClickedInventory().getHolder() != player) {
                return;
            }

            ShipNodeBreakable node = (ShipNodeBreakable) ship.getNode(player.getMetadata("repair-ui").getFirst().asInt());

            ItemStack is = event.getClickedInventory().getItem(event.getSlot());

            if (is == null) return;

            for (int i = 0; i < 8 && i < node.currentRepair.size(); i++) {
                RepairIngredient ri = node.currentRepair.get(i);
                if (!is.isEmpty() && ri.item == is.getType()) {
                    int dif = ri.amount - is.getAmount();
                    ri.amount = Math.max(dif, 0);
                    if (ri.amount == 0) node.currentRepair.remove(i);

                    if (dif == 0 || dif > 0) {
                        event.getClickedInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
                    } else {
                        event.getClickedInventory().setItem(event.getSlot(), new ItemStack(ri.item).asQuantity(Math.abs(dif)));
                    }
                    ship.getWorld().playSound(node.repairPos, Sound.BLOCK_HEAVY_CORE_PLACE, 1F, 0.3F);
                    if (node.currentRepair.isEmpty()) {
                        node.repair();
                        ship.getWorld().setBlockData(node.repairPos, Material.AIR.createBlockData());
                        ship.getWorld().playSound(node.repairPos, Sound.BLOCK_BEACON_ACTIVATE, 1F, 0.3F);
                        ship.getWorld().spawnParticle(Particle.ENCHANT, node.repairPos, 50);
                        event.getInventory().close();
                        player.closeInventory();
                    } else {
                        updateRepairScreen(node, event.getInventory());
                    }
                    return;
                }
            }
            ship.getWorld().playSound(node.repairPos, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1F, 0.6F);
            node.currentRepair = new ArrayList<>();

            for (RepairIngredient ingredient : node.repairIngredients) {
                node.currentRepair.add(new RepairIngredient(ingredient.amount, ingredient.item));
            }
            updateRepairScreen(node, event.getInventory());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("repair-ui")) {
            player.removeMetadata("repair-ui", plugin);
        }
    }

    @EventHandler
    public void onBlockBroken(BlockBreakEvent event) {
        if (!running || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
        if (event.getBlock().hasMetadata("spaceFarmBlock")) {
            event.getPlayer().give(event.getBlock().getDrops(event.getPlayer().getActiveItem()));
        }
    }

    public static boolean farmBuildingMode = false;

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        if (farmBuildingMode) {
            event.getBlock().getState().setMetadata("spaceFarmBlock", new FixedMetadataValue(plugin, true));
            event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1F, 1F);
        }
    }
}
