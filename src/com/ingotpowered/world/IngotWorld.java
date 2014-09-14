package com.ingotpowered.world;

import com.ingotpowered.api.Position;
import com.ingotpowered.api.definitions.Difficulty;
import com.ingotpowered.api.definitions.Dimension;
import com.ingotpowered.api.definitions.LevelType;
import com.ingotpowered.api.world.Block;
import com.ingotpowered.api.world.Chunk;
import com.ingotpowered.api.world.ChunkPosition;
import com.ingotpowered.api.world.World;

import java.util.concurrent.ConcurrentHashMap;

public class IngotWorld implements World {

    public String name;
    public ConcurrentHashMap<ChunkPosition, IngotChunk> chunks = new ConcurrentHashMap<ChunkPosition, IngotChunk>();
    public WorldThread worldThread = new WorldThread(this);
    public LevelType levelType;
    public Dimension dimension;
    public Difficulty difficulty;

    public IngotWorld(String name) {
        this.name = name;
        this.worldThread.start();
    }

    @Override
    public LevelType getType() {
        return levelType;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        synchronized (difficulty){
            this.difficulty = difficulty;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Chunk getChunkAt(ChunkPosition position) {
        return chunks.get(position);
    }

    @Override
    public Chunk getChunkAt(int x, int z) {
        return chunks.get(new ChunkPosition(x,z));
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        int cx = x>>4;
        int cz = z>>4;
        return getChunkAt(x,z).getBlock(x-(cx<<4),y,z-(cz<<4));
    }

    @Override
    public Block getBlock(double x, double y, double z) {
        return getBlock(x<0?((int)x) - 1 : (int)x, y<0?((int)y) - 1 : (int)y, z<0?((int)z) - 1 : (int)z);
    }

    @Override
    public Block getBlock(Position position) {
        return getBlock(position.getX(),position.getY(),position.getZ());
    }
}
