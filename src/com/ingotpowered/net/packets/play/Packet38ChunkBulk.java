package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet38ChunkBulk extends Packet {

    public boolean skylightInfo;
    public int chunkColumnCount;
    public int chunkXCoord;
    public int chunkZCoord;
    public short bitmask;

    public Packet38ChunkBulk(boolean skylightInfo, int chunkColumnCount, int chunkXCoord, int chunkZCoord, short bitmask, byte[] map) {
        this.skylightInfo = skylightInfo;
        this.chunkColumnCount = chunkColumnCount;
        this.chunkXCoord = chunkXCoord;
        this.chunkZCoord = chunkZCoord;
    }

    public void write(ByteBuf out) throws Exception {

    }

    public void handle(PacketHandler handler) { }
}
