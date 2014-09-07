package com.ingotpowered.net.http;

import com.ingotpowered.IngotServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.net.InetAddress;
import java.net.URI;
import java.nio.charset.Charset;

public class HttpPostRequest {

    private HttpHandler handler;

    public HttpPostRequest(String url, HttpHandler handler) {
        this.handler = handler;
        try {
            final URI uri = new URI(url);
            final StringBuilder response = new StringBuilder();
            Bootstrap b = new Bootstrap();
            b.channel(NioSocketChannel.class);
            b.group(IngotServer.server.netManager.workerGroup);
            b.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(new ReadTimeoutHandler(10));
                    SSLContext ssl = SSLContext.getInstance("TLS");
                    ssl.init(null, new TrustManager[] { DummyTrustManager.instance }, null);
                    SSLEngine engine = ssl.createSSLEngine();
                    engine.setUseClientMode(true);
                    channel.pipeline().addLast(new SslHandler(engine));
                    channel.pipeline().addLast(new HttpClientCodec());
                    channel.pipeline().addLast(new SimpleChannelInboundHandler<HttpObject>() {
                        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
                            HttpPostRequest.this.handler.onError(context.channel(), cause);
                        }
                        protected void messageReceived(ChannelHandlerContext context, HttpObject httpObject) throws Exception {
                            if (httpObject instanceof HttpResponse) {
                                HttpResponse resp = (HttpResponse) httpObject;
                                if (resp.getStatus().code() == HttpResponseStatus.NO_CONTENT.code()) {
                                    HttpPostRequest.this.handler.onSuccess(context, "");
                                } else if (resp.getStatus().code() != HttpResponseStatus.OK.code()) {
                                    HttpPostRequest.this.handler.onError(context.channel(), new Exception("Got incorrect status code!"));
                                }
                            } else if (httpObject instanceof HttpContent) {
                                HttpContent content = (HttpContent) httpObject;
                                response.append(content.content().toString(Charset.forName("UTF-8")));
                                if (content instanceof LastHttpContent) {
                                    HttpPostRequest.this.handler.onSuccess(context, response.toString());
                                }
                            }
                        }
                    });
                }
            });
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            b.remoteAddress(InetAddress.getByName(uri.getHost()), 443);
            b.connect().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        String path = uri.getRawPath() + ((uri.getRawQuery() == null ) ? "" : "?" + uri.getRawQuery());
                        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
                        request.headers().set(HttpHeaders.Names.HOST, uri.getHost());
                        channelFuture.channel().writeAndFlush(request);
                    } else {
                        HttpPostRequest.this.handler.onError(channelFuture.channel(), channelFuture.cause());
                    }
                }
            });
        } catch (Exception ex) {
            handler.onError(null, ex);
        }
    }
}
