package com.denisJava.testPlugin.spaceship.elements;

import com.denisJava.testPlugin.spaceship.ShipNode;
import com.denisJava.testPlugin.spaceship.ShipNodeStatus;
import org.bukkit.Location;

public class ShipPump extends ShipNode {
    public ShipPump(String name, Location sign) {
        super(name, sign);
    }

    @Override
    public ShipNodeStatus update(ShipStatus sh, ShipNodeStatus status) {
        if (sh.consumeEnergy(50)) {
            sh.produceWater(200);
            return ShipNodeStatus.OK;
        }
        return ShipNodeStatus.NOT_ENOUGH_RESOURCES;
    }
}
