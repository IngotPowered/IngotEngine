package com.ingotpowered.world;

import com.ingotpowered.api.Position;
import com.ingotpowered.api.world.Block;
import com.ingotpowered.api.world.Chunk;
import com.ingotpowered.api.world.ChunkPosition;
import com.ingotpowered.api.world.World;
import com.ingotpowered.entity.IngotEntity;

import java.util.Set;

public class IngotChunk implements Chunk {

    public static final int WIDTH = 16, DEPTH = 16, HEIGHT = 256;
    public static final int SIZE = WIDTH * HEIGHT * DEPTH;

    public World world;
    public byte[] blockIDs, blockData, skyLight, blockLight;
    public ChunkPosition position;
    public Set<IngotEntity> entities;

    public IngotChunk(ChunkPosition position) {
        this(position, new byte[SIZE]);
    }

    public IngotChunk(ChunkPosition position, byte[] chunkContent) {
        for(int i = 0; i < chunkContent.length; i++) chunkContent[i] = 2;
        this.position = position;
        this.blockIDs = chunkContent;
        this.blockData = new byte[SIZE];
        this.skyLight = new byte[SIZE];
        this.blockLight = new byte[SIZE];
    }

    private int getDataPosition(int x, int y, int z) {
        return y * (WIDTH * HEIGHT) + (z * WIDTH) + x;
    }

    private Position getPositionData(int data) {
        int rm = data % (WIDTH * HEIGHT);
        int y = (data - rm) / (WIDTH * HEIGHT);
        int rm2 = rm % WIDTH;
        int z = (rm - rm2) / WIDTH;
        return new Position(rm2, y, z);
    }

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

    public byte[] getChunkData() {

        byte[] data = new byte[(((4096 * 5 / 2) + 2048) * 16) + 256];
        //byte[] data = new byte[(4096 + 2048 + 2048 + 2048 + 0) * 16 + 256];
        int pos = 0;

        for (int i = 0; i < blockIDs.length; i++) {
            byte type = 2 << 4;// blockIDs[i];
            type = (byte)((type & 0xfff0) | blockData[i]);
            data[pos++] = (byte)(type & 0xff);
            data[pos++] = (byte)(type >> 8);
        }
        /*System.arraycopy(blockIDs, 0, data, 0, blockIDs.length);
        for (int i = 0; i < blockData.length; i += 2) {
            byte meta1 = blockData[i];
            byte meta2 = blockData[i + 1];
            data[pos++] = (byte) ((meta2 << 4) | meta1);
        }*/

        // skylight TODO
        for (int i = 0; i < skyLight.length; i += 2) {
            byte light1 = 15; //skyLight[i];
            byte light2 = 15; //skyLight[i + 1];
            data[pos++] = (byte) ((light2 << 4) | light1);
        }

        // blocklight TODO
        for (int i = 0; i < blockLight.length; i += 2) {
            byte light1 = 15; //blockLight[i];
            byte light2 = 15; //blockLight[i + 1];
            data[pos++] = (byte) ((light2 << 4) | light1);
        }


        // biome
        for (int i = 0; i < 256; i++)
            data[pos++] = 4; // TODO biome data, just set it to forest

        if (pos != data.length)
            throw new IllegalStateException("Illegal Pos: " + pos + " vs " + data.length);

        return data;
    }

}
