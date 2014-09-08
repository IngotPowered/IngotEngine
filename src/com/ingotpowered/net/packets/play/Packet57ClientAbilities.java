package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet57ClientAbilities extends Packet {

    public byte flags;
    public float flyingSpeed;
    public float walkingSpeed;

    public Packet57ClientAbilities(boolean godMode, boolean flyAllowed, boolean isFlying, boolean isCreative, float flyingSpeed, float walkingSpeed) {
        this.flags = (byte) ((godMode ? 8 : 0) | (flyAllowed ? 4 : 0) | (isFlying ? 2 : 0) | (isCreative ? 1 : 0));
        this.flyingSpeed = flyingSpeed;
        this.walkingSpeed = walkingSpeed;
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 57);
        out.writeByte(flags);
        out.writeFloat(flyingSpeed);
        out.writeFloat(walkingSpeed);
    }

    public void handle(PacketHandler handler) { }
}
