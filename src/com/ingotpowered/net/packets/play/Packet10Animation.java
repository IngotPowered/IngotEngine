package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;

public class Packet10Animation extends Packet {

    public void handle(PacketHandler handler) {
        handler.playerAnimation(this);
    }
}
