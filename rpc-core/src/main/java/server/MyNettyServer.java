package server;

import common.Request;
import common.Response;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import others.Decoder;
import others.Encoder;
import others.RegisterCenter;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class MyNettyServer {
    private int port;
    private int nThreads;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;

    public MyNettyServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
    }

    public void start() {
        parentGroup = new NioEventLoopGroup(nThreads);
        childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new Decoder(Request.class));
                            ch.pipeline().addLast(new Encoder(Response.class));
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 接收连接
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Rpc服务启动成功 " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }

    }

    public void stop() {
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }

}

class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        Request request = (Request) msg;


        // 检查
        Class clazz = RegisterCenter.getService(((Request) msg).getServiceName());
        if (clazz == null) {
            System.out.println("clazz is null");
        }

        Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
        if (method == null) {
            System.out.println(" method is null");
        }

        Object resp = method.invoke(clazz.newInstance(), request.getArgs());

        Response rsp = new Response(resp);
        ctx.writeAndFlush(rsp).addListener(ChannelFutureListener.CLOSE);
        System.out.println("Server send response: " + rsp.getResult());

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
