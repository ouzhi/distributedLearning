package nettyinaction.unittest;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class FixLengthFrameDecoder extends ByteToMessageDecoder {

	private final int frameLength;
	
	public FixLengthFrameDecoder(int frameLength) {
		if (frameLength <= 0) {
			throw new IllegalArgumentException("frame length must a positive number!" + frameLength);
		}
		this.frameLength = frameLength;
		
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() >= frameLength) {
			out.add(in.readBytes(frameLength));
		}
	}

}
