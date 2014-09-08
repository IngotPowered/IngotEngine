package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet4Position extends Packet {

    public double x;
    public double feetY;
    public double z;
    public boolean onGround;

    public void read(ByteBuf in) throws Exception {
        x = in.readDouble();
        feetY = in.readDouble();
        z = in.readDouble();
        onGround = in.readBoolean();
    }

    public void handle(PacketHandler handler) {
        handler.positionUpdate(this);
    }
}
