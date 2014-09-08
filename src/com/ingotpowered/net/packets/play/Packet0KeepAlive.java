package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet0KeepAlive extends Packet {

    public int id;

    public void read(ByteBuf in) throws Exception {
        id = PacketConstants.readVarInt(in);
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 0);
        PacketConstants.writeVarInt(out, id);
    }

    public void handle(PacketHandler handler) {
        handler.keepAlive(this);
    }
}
