package client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private Object result;

    public Object getResult() {
        return result;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        result = msg;
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //System.out.println("-------NettyClientHandler.channelActive---------");
    }

}
