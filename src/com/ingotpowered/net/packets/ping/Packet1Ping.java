package com.ingotpowered.net.packets.ping;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet1Ping extends Packet {

    public long timestamp;

    public void read(ByteBuf in) throws Exception {
        timestamp = in.readLong();
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 1);
        out.writeLong(timestamp);
    }

    public void handle(PacketHandler handler) {
        handler.ping(this);
    }
}
