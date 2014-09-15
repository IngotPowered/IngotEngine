package com.ingotpowered.world;

import com.ingotpowered.api.Position;
import com.ingotpowered.api.world.Block;
import com.ingotpowered.api.world.Chunk;
import com.ingotpowered.api.world.World;

public class IngotBlock implements Block
{
    public Chunk chunk;
    public Position position;

    public IngotBlock(Chunk chunk, int x, int y, int z) {
        this.chunk = chunk;
        position = new Position(x, y, z);
    }

    @Override
    public World getWorld() {
        return chunk.getWorld();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public String getType() {
        return "GRASS_BLOCK";
    }

    @Override
    public byte getID() {
        return 2;
    }
}
