package com.ingotpowered.net;

import com.ingotpowered.IngotPlayer;
import com.ingotpowered.net.codec.VarIntCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.ConcurrentHashMap;

public class NetManager {

    public ConcurrentHashMap<String, IngotPlayer> playerMap;
    public EventLoopGroup bossGroup = new NioEventLoopGroup();
    public EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    public ChannelFuture future;

    public NetManager(ConcurrentHashMap<String, IngotPlayer> playerMap) {
        this.playerMap = playerMap;
    }

    public void start() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    IngotPlayer ingotPlayer = new IngotPlayer(ch);
                    ch.pipeline().addLast(new ReadTimeoutHandler(15));
                    ch.pipeline().addLast(new VarIntCodec());
                    ch.pipeline().addLast(ingotPlayer.packetCodec);
                }
            });
            b.option(ChannelOption.SO_BACKLOG, 16);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            future = b.bind(25565);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        future.channel().close().syncUninterruptibly();
    }
}
