package com.ingotpowered.net.packets;

import com.ingotpowered.net.PacketHandler;
import io.netty.buffer.ByteBuf;

public abstract class Packet {

    public void read(ByteBuf buf) throws Exception { }

    public void write(ByteBuf out) throws Exception { }

    public abstract void handle(PacketHandler handler);
}
