package nettyLearning.chapter3.baseNetty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyTimeServerHanler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ByteBuf time = ctx.alloc().buffer(4);
		time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
		
		ChannelFuture channelFuture = ctx.writeAndFlush(time);
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				assert channelFuture == future;
				ctx.close();
			}
		});
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
