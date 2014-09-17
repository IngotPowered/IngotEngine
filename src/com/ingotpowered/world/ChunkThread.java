package com.ingotpowered.world;

import com.ingotpowered.entity.IngotEntity;

public class ChunkThread extends Thread {

    public ChunkThread(IngotChunk chunk){
        this.chunk = chunk;
    }

    private IngotChunk chunk;

    public void run() {
        for(IngotEntity e : chunk.entities){
            e.tick();
        }
    }
}
