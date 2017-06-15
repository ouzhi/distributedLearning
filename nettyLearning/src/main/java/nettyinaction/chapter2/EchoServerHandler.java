package nettyinaction.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	/*
	 * netty简化了网络编程的 工作，每个处理类只需要覆盖 hook钩子即可
	 * 关注点分离，关注自己需要的部分
	 * */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//msg对象根据ChannelHandler处理链上结果的不同，实际类型不同
		ByteBuf buf = (ByteBuf) msg;
		System.out.println("Server received : " + buf.toString(CharsetUtil.UTF_8));
		ctx.write(buf);
//		ReferenceCountUtil.release(msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
			.addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
