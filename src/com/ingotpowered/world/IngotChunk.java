package com.ingotpowered.world;

import com.ingotpowered.api.world.Chunk;
import com.ingotpowered.api.world.ChunkPosition;
import com.ingotpowered.api.world.World;

public class IngotChunk implements Chunk{

    public World world;
    public byte[] chunkData;
    public ChunkPosition position;

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public ChunkPosition getPosition() {
        return position;
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public int getZ() {
        return position.getZ();
    }
}
