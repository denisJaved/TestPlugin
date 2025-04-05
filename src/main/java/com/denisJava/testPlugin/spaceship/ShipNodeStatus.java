package com.denisJava.testPlugin.spaceship;

import net.kyori.adventure.text.Component;

public enum ShipNodeStatus {

    OK(true, Component.text("В Работе")),
    WAITING_FOR_RESOURCES(true, Component.text("Нет ресурсов")),

    HAND_DISABLED(false, Component.text("Отключён")),
    BROKEN(false, Component.text("Сломан")),
    NOT_BUILT_YET(false, Component.text("Не построен"))

    ;final boolean shouldUpdate;
    final Component text;
    ShipNodeStatus(boolean shouldUpdate, Component text) {
        this.shouldUpdate = shouldUpdate;
        this.text = text;
    }
}
