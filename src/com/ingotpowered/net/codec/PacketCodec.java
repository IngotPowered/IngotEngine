package com.ingotpowered.net.codec;

import com.ingotpowered.net.*;
import com.ingotpowered.net.packets.*;
import com.ingotpowered.net.packets.handshake.Packet0Handshake;
import com.ingotpowered.net.packets.login.Packet0LoginStart;
import com.ingotpowered.net.packets.login.Packet1Encryption;
import com.ingotpowered.net.packets.ping.Packet0Status;
import com.ingotpowered.net.packets.ping.Packet1Ping;
import com.ingotpowered.net.packets.play.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class PacketCodec extends ByteToMessageCodec<Packet> {

    public ProtoState protoState = ProtoState.HANDSHAKE;
    public PacketHandler handler;

    public PacketCodec(PacketHandler handler) {
        this.handler = handler;
    }

    protected void encode(ChannelHandlerContext context, Packet packet, ByteBuf buf) throws Exception {
        packet.write(buf);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Using switch to construct class normally rather than mapping classes results in a 56% to 430% speed increase
        int ident = PacketConstants.readVarInt(in);
        Packet packet = null;
        if (protoState == ProtoState.HANDSHAKE) {
            switch (ident) {
                case 0:
                    packet = new Packet0Handshake(); break;
                default:
                    throw new Exception("Unknown packet ID during HANDSHAKE " + ident);
            }
        } else if (protoState == ProtoState.PING) {
            switch (ident) {
                case 0:
                    packet = new Packet0Status(); break;
                case 1:
                    packet = new Packet1Ping(); break;
                default:
                    throw new Exception("Unknown packet ID during PING " + ident);
            }
        } else if (protoState == ProtoState.LOGIN) {
            switch (ident) {
                case 0:
                    packet = new Packet0LoginStart(); break;
                case 1:
                    packet = new Packet1Encryption(); break;
                default:
                    throw new Exception("Unknown packet ID during LOGIN " + ident);
            }
        } else if (protoState == ProtoState.PLAY) {
            switch (ident) {
                case 0:
                    packet = new Packet0KeepAlive(); break;
                case 1:
                    packet = new PacketChat(); break;
                case 4:
                    packet = new Packet4Position(); break;
                case 6:
                    packet = new Packet6PosOrient(); break;
                case 21:
                    packet = new Packet15ClientSettings(); break;
                case 23:
                    packet = new PacketPluginMessage(); break;
                default:
                    System.out.println("Unknown packet ID " + ident);
                    throw new Exception("Unknown packet ID during PLAY " + ident);
            }
        }
        packet.read(in);
        packet.handle(handler);
    }

    public void channelInactive(ChannelHandlerContext context) {
        handler.ingotPlayer.playerDisconnected();
    }

    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        context.close();
    }
}