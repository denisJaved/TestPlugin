package com.denisJava.testPlugin.legacy.spaceship.elements;

import com.denisJava.testPlugin.legacy.spaceship.ShipNode;
import com.denisJava.testPlugin.legacy.spaceship.ShipNodeStatus;
import org.bukkit.Location;

public class ShipPump extends ShipNode {
    public ShipPump(String name, Location sign) {
        super(name, sign);
    }

    @Override
    public ShipNodeStatus update(ShipStatus sh, ShipNodeStatus status) {
        if (sh.consumeEnergy(25)) {
            sh.produceWater(100);
            return ShipNodeStatus.OK;
        }
        return ShipNodeStatus.NOT_ENOUGH_RESOURCES;
    }
}
