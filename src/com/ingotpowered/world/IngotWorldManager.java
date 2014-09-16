package com.ingotpowered.world;

import com.ingotpowered.IngotServer;
import com.ingotpowered.api.world.WorldManager;

import java.util.concurrent.ConcurrentHashMap;

public class IngotWorldManager implements WorldManager {

    public ConcurrentHashMap<String, IngotWorld> worldMap = new ConcurrentHashMap<String, IngotWorld>();

    public IngotWorld getDefaultWorld() {
        return getExistingWorld(IngotServer.server.config.getDefaultLevelName());
    }

    public IngotWorld getExistingWorld(String name) {
        return worldMap.get(name.toLowerCase());
    }

    public IngotWorld createWorldSync(String name) {
        if (getExistingWorld(name) != null) {
            throw new RuntimeException("World " + name + " already exists!");
        }
        IngotWorld world = new IngotWorld(name);
        synchronized (worldMap) {
            worldMap.put(name.toLowerCase(), world);
        }
        return world;
    }
}
