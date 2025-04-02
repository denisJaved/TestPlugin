package com.denisJava.testPlugin.spaceship;

import com.denisJava.testPlugin.spaceship.elements.ShipStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.jetbrains.annotations.NotNull;

public abstract class ShipNode {
    public String name;
    public Location signPos;
    public boolean shouldStop = false;

    public ShipNode(String name, Location sign) {
        this.name = name;
        this.signPos = sign;
    }

    public ShipNodeStatus fullUpdate(ShipStatus sh, int id, ShipNodeStatus _status) {
        ShipNodeStatus status;

        if (shouldStop) status = ShipNodeStatus.DISABLED;
        else status = update(sh, _status);

        Sign sign_ = (Sign) signPos.getBlock().getState();
        SignSide sign = sign_.getSide(Side.FRONT);
        sign.line(0, Component.text("[" + id + "] ").color(TextColor.color(50, 50, 50)).append(Component.text(name).color(TextColor.color(0, 0, 0))));
        switch (status) {
            case OK -> {
                sign.line(1, Component.text("Активен").color(TextColor.color(0, 255, 0)));
            }
            case NOT_ENOUGH_RESOURCES -> {
                sign.line(1, Component.text("Нет ресурсов").color(TextColor.color(255, 0, 0)));
            }
            case DISABLED -> {
                sign.line(1, Component.text("Отключен").color(TextColor.color(50, 50, 50)));
            }
            case NOT_BUILT_YET -> {
                sign.line(1, Component.text("Не построен").color(TextColor.color(50, 50, 50)));
            }
            case BROKEN, REPAIRING -> {
                sign.line(1, Component.text("Сломан").color(TextColor.color(255, 0, 0)));
            }
        }
        sign.line(3, getFourthLine());
        sign_.update();

        status = updateMixin(sh, id, status);

        return status;
    }

    public abstract ShipNodeStatus update(ShipStatus sh, ShipNodeStatus status);
    public ShipNodeStatus updateMixin(ShipStatus sh, int id, ShipNodeStatus status) {
        return status;
    }

    public @NotNull Component getFourthLine() {
        return Component.empty();
    }
}
