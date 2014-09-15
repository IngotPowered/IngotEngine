package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet9HeldItem extends Packet {

    public int slotNumber = 0;

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 9);
        out.writeByte(slotNumber);
    }

    public void read(ByteBuf in) throws Exception {
        this.slotNumber = in.readShort();
    }

    public void handle(PacketHandler handler) {
        handler.heldItemChange(this);
    }
}
