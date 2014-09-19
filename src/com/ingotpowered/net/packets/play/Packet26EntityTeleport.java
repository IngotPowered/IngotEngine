package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet26EntityTeleport extends Packet {

    public Packet26EntityTeleport(int id, double x, double y, double z, float yaw, float pitch, boolean onGround){
        this.id = id;
        this.x = (int) (x*32);
        this.y = (int) (y*32);
        this.z = (int) (z*32);
        this.yaw = (byte) (yaw/360*255);
        this.pitch = (byte) (pitch/360*255);
        this.onGround = onGround;
    }

    public int id;
    public int x;
    public int y;
    public int z;
    public byte yaw;
    public byte pitch;
    public boolean onGround;

    @Override
    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out,26);
        PacketConstants.writeVarInt(out,id);
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(z);
        out.writeByte(yaw);
        out.writeByte(pitch);
        out.writeBoolean(onGround);
    }

    @Override
    public void handle(PacketHandler handler) {

    }
}
