package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet8BlockPlace extends Packet {

    public long position;
    public byte direction;
    public short itemId;
    public byte amount;
    public short damage;
    public byte cursorX;
    public byte cursorY;
    public byte cursorZ;

    public void read(ByteBuf in) throws Exception {
        position = in.readLong();
        direction = in.readByte();

        // Read slot data item ID
        itemId = in.readShort();
        if (itemId > -1) {
            amount = in.readByte();
            damage = in.readShort();
            byte hasNbt = in.readByte();
            if (hasNbt != 0) {
                throw new Exception("Did not read NBT!");
            }
        }

        cursorX = in.readByte();
        cursorY = in.readByte();
        cursorZ = in.readByte();
    }

    public void handle(PacketHandler handler) {
        handler.blockPlace(this);
    }
}
