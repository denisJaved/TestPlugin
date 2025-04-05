package com.denisJava.testPlugin.spaceship;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public abstract class ShipNode {
    private Ship ship;
    private ShipNodeStatus status = ShipNodeStatus.OK;
    private ShipResourceStack[] requiredResources;
    private int durability;
    private final String name;

    public ShipNode(Ship ship, ShipResourceStack[] requiredResources, String name) {
        this.ship = ship;
        this.requiredResources = requiredResources;
        this.name = name;
    }

    public ShipNodeStatus getStatus() {
        return status;
    }

    public void setStatus(ShipNodeStatus status) {
        this.status = status;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }
    public boolean checkResourceRequirement() {
        for (ShipResourceStack resourceStack : requiredResources) {
            if (ship.get(resourceStack.resource) < resourceStack.amount) return false;
        }
        return true;
    }
    public void consume() {
        for (ShipResourceStack resourceStack : requiredResources) {
            ship.consume(resourceStack.resource, resourceStack.amount);
        }
    }
    public boolean checkAndConsume() {
        if (checkResourceRequirement()) {
            consume();
            return true;
        }
        return false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Component report() {
        Component component = Component.text(SFXUtils.Pipe_H+SFXUtils.Pipe_H+SFXUtils.Pipe_H+"[ ").color(SFXUtils.WHITE)
                .append(Component.text(name).color(status.text.color()))
                .append(Component.text(" ]"+SFXUtils.Pipe_H+SFXUtils.Pipe_H+SFXUtils.Pipe_H+"\n").color(SFXUtils.WHITE))
                .append(Component.text(" Цена работы:\n").color(SFXUtils.GOLD));

        for (ShipResourceStack resourceStack : requiredResources) {
            component.append(Component.text("- ").color(SFXUtils.WHITE))
                    .append(Component.text(resourceStack.amount + " * "))
                    .append(resourceStack.resource.userFriendlyName);
        }

        return component;
    }

    public abstract int getMaxDurability();
    public abstract void update();
}
