package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet6PosOrient extends Packet {

    public double x;
    public double feetY;
    public double z;
    public float yaw;
    public float pitch;
    public boolean grounded;

    public void read(ByteBuf in) throws Exception {
        x = in.readDouble();
        feetY = in.readDouble();
        z = in.readDouble();
        yaw = in.readFloat();
        pitch = in.readFloat();
        grounded = in.readBoolean();
    }

    public void handle(PacketHandler handler) {
        handler.positionAndOrientationUpdate(this);
    }
}
