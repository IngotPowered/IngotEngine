package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet3GroundStatus extends Packet {

    public boolean onGround;

    public void read(ByteBuf in) throws Exception {
        this.onGround = in.readBoolean();
    }

    public void handle(PacketHandler handler) {
        handler.groundStatus(this);
    }
}
