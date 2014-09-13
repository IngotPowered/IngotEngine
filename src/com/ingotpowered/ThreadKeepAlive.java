package com.ingotpowered;

import com.ingotpowered.net.packets.play.Packet0KeepAlive;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class ThreadKeepAlive extends Thread {

    public Random random = new Random(System.currentTimeMillis());

    public void run() {
        while (true) {
            try {
                Thread.sleep(11000);
            } catch (Exception ex) {
                break;
            }
            Collection<IngotPlayer> players = IngotServer.server.playerMap.values();
            Iterator<IngotPlayer> iterator = players.iterator();
            while (iterator.hasNext()) {
                IngotPlayer p = iterator.next();
                if (System.currentTimeMillis() - p.packetHandler.waitingPingId >= 30000) {
                    p.kick("Did not respond to ping in time");
                    continue;
                }
                p.packetHandler.waitingPingId = random.nextInt();
                p.channel.pipeline().writeAndFlush(new Packet0KeepAlive(p.packetHandler.waitingPingId)).syncUninterruptibly();
                p.packetHandler.pingSentTimestamp = System.currentTimeMillis();
            }
        }
        System.out.println("Player keep-alive thread shut down.");
    }
}
