package nettyinaction.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

	private final ChannelGroup channelgroup;
	
	public TextWebSocketFrameHandler(ChannelGroup channelGroup) {
		this.channelgroup = channelGroup;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
			System.out.println(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete);
			ctx.pipeline().remove(HttpRequestHandler.class);
			channelgroup.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined!"));
			channelgroup.add(ctx.channel());
		} else {
			super.userEventTriggered(ctx, evt);
		}
			
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		channelgroup.writeAndFlush(msg.retain());
	}

}
