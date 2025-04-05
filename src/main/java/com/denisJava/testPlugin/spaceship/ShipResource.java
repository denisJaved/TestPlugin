package com.denisJava.testPlugin.spaceship;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum ShipResource {

    WATER(Component.text("Вода").color(TextColor.color(20, 20, 255))),
    ELECTRICITY(Component.text("Электричевство").color(TextColor.color(255, 50, 50))),
    OXYGEN(Component.text("Воздух").color(TextColor.color(90, 90, 100)))

    ;final Component userFriendlyName;

    ShipResource(Component userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }
}
