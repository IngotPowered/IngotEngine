package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet24LookAndRelativeMove extends Packet {

    public Packet24LookAndRelativeMove(int id, double x, double y, double z, float yaw, float pitch, boolean onGround){
        this.id = id;
        this.x = (byte) (x*32);
        this.y = (byte) (y*32);
        this.z = (byte) (z*32);
        this.yaw = (byte) (yaw/360*255);
        this.pitch = (byte) (pitch/360*255);
        this.onGround = onGround;
    }

    public int id;
    public byte x;
    public byte y;
    public byte z;
    public byte yaw;
    public byte pitch;
    public boolean onGround;

    @Override
    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out,24);
        PacketConstants.writeVarInt(out,id);
        out.writeByte(x);
        out.writeByte(y);
        out.writeByte(z);
        out.writeByte(yaw);
        out.writeByte(pitch);
        out.writeBoolean(onGround);
    }

    @Override
    public void handle(PacketHandler handler) {

    }
}
