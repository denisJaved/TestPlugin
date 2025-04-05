package com.denisJava.testPlugin.spaceship;

import java.util.HashMap;

public class Ship {
    // Resources
    private final HashMap<ShipResource, Integer> resourceHolder;

    // Infrastructure
    private ShipNode[] nodes;

    public Ship() {
        resourceHolder = new HashMap<>();
    }

    public int get(ShipResource resource) {return resourceHolder.get(resource);}
    public void set(ShipResource resource, int amount) {resourceHolder.put(resource, amount);}
    public void produce(ShipResource resource, int amount) {resourceHolder.put(resource, resourceHolder.get(resource) + amount);}
    public boolean consume(ShipResource resource, int amount) {if(get(resource)>=amount){produce(resource, -amount);return true;}return false;}
}
