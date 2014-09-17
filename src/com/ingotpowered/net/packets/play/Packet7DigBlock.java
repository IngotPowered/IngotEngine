package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet7DigBlock extends Packet {

    public byte status;
    public long position;
    public byte face;

    public void read(ByteBuf in) throws Exception {
        status = in.readByte();
        position = in.readLong();
        face = in.readByte();
    }

    public void handle(PacketHandler handler) {
        handler.digBlockStatus(this);
    }
}
