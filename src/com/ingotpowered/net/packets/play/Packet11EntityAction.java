package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet11EntityAction extends Packet {

    public int entityId;
    public byte actionId;
    public int jumpBoost;

    public void read(ByteBuf in) throws Exception {
        entityId = PacketConstants.readVarInt(in);
        actionId = in.readByte();
        jumpBoost = PacketConstants.readVarInt(in);
    }

    public void handle(PacketHandler handler) {
        handler.entityAction(this);
    }
}
