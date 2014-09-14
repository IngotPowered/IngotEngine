package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet38ChunkBulk extends Packet {

    public boolean skylightInfo;
    public int chunkColumnCount;
    public int chunkXCoord;
    public int chunkZCoord;
    public short bitmask;
    public byte[] map;

    public Packet38ChunkBulk(boolean skylightInfo, int chunkColumnCount, int chunkXCoord, int chunkZCoord, short bitmask, byte[] map) {
        this.skylightInfo = skylightInfo;
        this.chunkColumnCount = chunkColumnCount;
        this.chunkXCoord = chunkXCoord;
        this.chunkZCoord = chunkZCoord;
        this.bitmask = bitmask;
        this.map = map;
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 38);
        out.writeBoolean(this.skylightInfo);
        PacketConstants.writeVarInt(out, this.chunkColumnCount);
        out.writeInt(this.chunkXCoord);
        out.writeInt(this.chunkZCoord);
        out.writeShort(this.bitmask);
        PacketConstants.writeVarInt(out, this.map.length);
        out.writeBytes(this.map);
    }

    public void handle(PacketHandler handler) { }
}
