package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet23EntityLook extends Packet {

    public Packet23EntityLook(int entityId, float yaw, float pitch, boolean onGround){
        this.id = entityId;
        this.yaw = (byte) ((yaw/360F)*255);
        this.pitch = (byte) ((pitch/360F)*255);
        this.onGround = onGround;
    }

    public int id;
    public byte yaw;
    public byte pitch;
    public boolean onGround;


    @Override
    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 22);
        PacketConstants.writeVarInt(out, id);
        out.writeByte(yaw);
        out.writeByte(pitch);
        out.writeBoolean(onGround);
    }

    @Override
    public void handle(PacketHandler handler) {

    }
}
