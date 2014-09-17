package com.ingotpowered.world;

import com.ingotpowered.api.world.Block;
import com.ingotpowered.api.world.Chunk;
import com.ingotpowered.api.world.ChunkPosition;
import com.ingotpowered.api.world.World;
import com.ingotpowered.entity.IngotEntity;

import java.util.Set;

public class IngotChunk implements Chunk{

    public World world;
    public byte[] chunkData;
    public ChunkPosition position;
    public Set<IngotEntity> entities;

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

    //TBH.. not sure how blocks/other data is getting stored
    @Override
    public Block getBlock(int x, int y, int z) {
        //TODO: Write block retrieval
        return null;
    }
}
