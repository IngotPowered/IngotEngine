package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet70Compression extends Packet {

    public int compressionLevel = -1;

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 70);
        PacketConstants.writeVarInt(out, compressionLevel);
    }

    public void handle(PacketHandler handler) { }
}
