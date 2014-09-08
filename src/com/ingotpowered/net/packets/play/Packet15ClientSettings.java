package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet15ClientSettings extends Packet {

    public String locale;
    public byte viewDistance;
    public byte chatFlags;
    public boolean showChatColors;
    public byte displaySkinParts;

    public void read(ByteBuf in) throws Exception {
        locale = PacketConstants.readString(in);
        viewDistance = in.readByte();
        chatFlags = in.readByte();
        showChatColors = in.readBoolean();
        displaySkinParts = in.readByte();
    }

    public void handle(PacketHandler handler) {
        handler.clientSettings(this);
    }
}
