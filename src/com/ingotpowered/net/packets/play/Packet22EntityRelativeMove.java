package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet22EntityRelativeMove extends Packet {

    public Packet22EntityRelativeMove(int entityId, double x, double y, double z, boolean onGround){
        this.id = entityId;
        this.x = (byte) (x*32);
        this.y = (byte) (y*32);
        this.z = (byte) (z*32);
        this.onGround = onGround;
    }

    public int id;
    public byte x;
    public byte y;
    public byte z;
    public boolean onGround;


    @Override
    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out,22);
        PacketConstants.writeVarInt(out,id);
        out.writeByte(x);
        out.writeByte(y);
        out.writeByte(z);
        out.writeBoolean(onGround);
    }

    @Override
    public void handle(PacketHandler handler) {

    }
}
