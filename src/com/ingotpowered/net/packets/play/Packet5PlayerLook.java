package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet5PlayerLook extends Packet {

    public float yaw;
    public float pitch;
    public boolean onGround;

    public void read(ByteBuf in) throws Exception {
        yaw = in.readFloat();
        pitch = in.readFloat();
        onGround = in.readBoolean();
    }

    public void handle(PacketHandler handler) {
        handler.playerLook(this);
    }
}
