package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet2UseEntity extends Packet {

    public int target;
    public int type;
    public float targetX;
    public float targetY;
    public float targetZ;

    public void read(ByteBuf in) throws Exception {
        target = PacketConstants.readVarInt(in);
        type = PacketConstants.readVarInt(in);
        targetX = in.readFloat();
        targetY = in.readFloat();
        targetZ = in.readFloat();
    }

    public void handle(PacketHandler handler) {
        handler.playerUseEntity(this);
    }
}
