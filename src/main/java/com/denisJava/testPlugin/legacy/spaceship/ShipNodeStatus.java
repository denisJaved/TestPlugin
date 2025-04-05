package com.denisJava.testPlugin.legacy.spaceship;

public enum ShipNodeStatus {
    // Update
    OK(true), NOT_ENOUGH_RESOURCES(true), DISABLED(true),

    // No updates
    NOT_BUILT_YET, BROKEN, REPAIRING;

    public boolean shouldUpdate = false;

    ShipNodeStatus(boolean shouldUpdate) {
        this.shouldUpdate = shouldUpdate;
    }

    ShipNodeStatus() {
    }
}
