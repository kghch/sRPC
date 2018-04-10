package client;

import common.Request;
import common.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import others.Decoder;
import others.Encoder;

import java.net.InetSocketAddress;

public class MyNettyClient2 {

    public static Object send(Request rpcRequest, InetSocketAddress inetSocketAddress) {
        NettyClientHandler myClientHandler = new NettyClientHandler();
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new Encoder(Request.class));
                            ch.pipeline().addLast(new Decoder(Response.class));
                            ch.pipeline().addLast(myClientHandler);
                        }
                    });

            ChannelFuture future = b.connect(inetSocketAddress.getAddress(), inetSocketAddress.getPort()).sync();
            future.channel().writeAndFlush(rpcRequest);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return myClientHandler.getResult();

    }
}
