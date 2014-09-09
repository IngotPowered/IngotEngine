package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class PacketPlayerPosLook extends Packet {

    public double x;
    public double feetY;
    public double z;
    public float yaw;
    public float pitch;
    public byte flags;
    public boolean grounded;

    public PacketPlayerPosLook() { }

    public PacketPlayerPosLook(double x, double y, double z, float yaw, float pitch, byte flags) {
        this.x = x;
        this.feetY = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 8);
        out.writeDouble(x);
        out.writeDouble(feetY);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);
        out.writeByte(flags);
    }

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
