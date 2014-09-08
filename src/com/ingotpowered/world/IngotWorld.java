package com.ingotpowered.world;

import com.ingotpowered.api.world.ChunkPosition;
import com.ingotpowered.api.world.World;

import java.util.concurrent.ConcurrentHashMap;

public class IngotWorld implements World {

    public String name;
    public ConcurrentHashMap<ChunkPosition, IngotChunk> chunks = new ConcurrentHashMap<ChunkPosition, IngotChunk>();
    public WorldThread worldThread = new WorldThread(this);

    public IngotWorld(String name) {
        this.name = name;
        this.worldThread.start();
    }
}
