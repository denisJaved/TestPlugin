package com.denisJava.testPlugin;

import com.denisJava.testPlugin.spaceship.GameLoop;
import com.denisJava.testPlugin.spaceship.SFXUtils;
import com.denisJava.testPlugin.spaceship.ShipNodeBreakable;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

@SuppressWarnings("UnstableApiUsage")
public class CommandRegistry {
    public static void register(LifecycleEventManager<Plugin> lifecycleManager) {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LiteralCommandNode<CommandSourceStack> cmd = Commands.literal("test")
                    .then(Commands.literal("explode")
                            .requires(r -> r.getSender().hasPermission("storyanvil.command.explode"))
                            .executes(explode)
                    )
                    .then(Commands.literal("parkour")
                            .then(Commands.literal("spawn")
                                    .executes(parkour)
                            )
                            .then(Commands.literal("stop")
                                    .executes(stopParkour)
                            )
                            .then(Commands.literal("iceBlocks")
                                    .then(Commands.argument("value", BoolArgumentType.bool())
                                            .executes(parkourIce)
                                    )
                            )
                    )
                    .then(Commands.literal("door")
                            .then(Commands.argument("x", IntegerArgumentType.integer())
                                    .then(Commands.argument("y", IntegerArgumentType.integer())
                                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                                    .then(Commands.argument("open", BoolArgumentType.bool())
                                                            .executes(context -> {
                                                                SFXUtils.door(
                                                                        context.getSource().getLocation().getWorld(),
                                                                        Axis.Z,
                                                                        BoolArgumentType.getBool(context, "open"),
                                                                        new Vector(
                                                                                IntegerArgumentType.getInteger(context, "x"),
                                                                                IntegerArgumentType.getInteger(context, "y"),
                                                                                IntegerArgumentType.getInteger(context, "z")
                                                                        )
                                                                );
                                                                return 0;
                                                            })
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(Commands.literal("spaceship")
                            .then(Commands.literal("start").executes(context -> {GameLoop.startLoop(context.getSource().getLocation().getWorld());return 0;}))
                            .then(Commands.literal("stop").executes(context -> {GameLoop.stopLoop();return 0;}))
                            .then(Commands.literal("node").then(Commands.argument("id", IntegerArgumentType.integer(0))
                                    .then(Commands.literal("durability")
                                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                    .executes(context -> {
                                                        ((ShipNodeBreakable) GameLoop.ship.getNode(IntegerArgumentType.getInteger(context, "id")))
                                                                .durability = IntegerArgumentType.getInteger(context, "value");
                                                        return 0;
                                                    })
                                            )
                                    )
                            ))
                    )
                    .build();
            event.registrar().register(cmd);
        });
    }

    public static final Command<CommandSourceStack> explode = context -> {
        return context.getSource().getLocation().createExplosion(5, false, false) ? 0 : 1;
    };
    public static final Command<CommandSourceStack> parkour = context -> {
        if (context.getSource().getExecutor() instanceof Player p) p.getInventory().clear();
        World world = context.getSource().getLocation().getWorld();
        Location location = context.getSource().getLocation().toBlockLocation().add(0, -1, 0);
        world.setBlockData(location, Material.GOLD_BLOCK.createBlockData());
        TestPlugin.currentParkourBlock = location.toVector();
        if (context.getSource().getExecutor() instanceof Player p) p.showTitle(Title.title(Component.text("Паркур начат").color(TextColor.color(0, 255, 0)), Component.text("Прыгай на ").append(Component.text("золотые блоки").color(TextColor.color(255, 255, 0)))));
        return 0;
    };
    public static final Command<CommandSourceStack> stopParkour = context -> {
        if (context.getSource().getExecutor() instanceof Player p) p.getInventory().clear();
        World world = context.getSource().getLocation().getWorld();
        world.setBlockData(TestPlugin.currentParkourBlock.toLocation(world), Material.AIR.createBlockData());
        world.setBlockData(TestPlugin.currentParkourBlock.add(TestPlugin.lastParkourOffset).toLocation(world), Material.OBSIDIAN.createBlockData());
        TestPlugin.currentParkourBlock = new Vector();
        TestPlugin.lastParkourOffset = new Vector(0, 1, 0);
        if (context.getSource().getExecutor() instanceof Player p) p.showTitle(Title.title(Component.text("Паркур закончен").color(TextColor.color(0, 255, 0)), Component.text("")));
        return 0;
    };
    public static final Command<CommandSourceStack> parkourIce = context -> {
        TestPlugin.iceBlocks = BoolArgumentType.getBool(context, "value");
        return 0;
    };
}
