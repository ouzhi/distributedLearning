package nettyinaction.selfcodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MemcachedRequestEncoder extends MessageToByteEncoder<MemcachedRequest> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MemcachedRequest msg, ByteBuf out) throws Exception {
		byte[] key = msg.getKey().getBytes(CharsetUtil.UTF_8);
		byte[] body = msg.getBody().getBytes(CharsetUtil.UTF_8);
		// totalSize = key size + body size + extras size
		int bodySize = key.length + body.length + (msg.hasExtras() ? 8 : 0);
		
		out.writeByte(msg.getMagic());
		out.writeByte(msg.getOpCode());
		out.writeShort(key.length);
		int extrasSize = msg.hasExtras() ? 0x80 : 0x00;
		out.writeByte(extrasSize);
		out.writeByte(0);
		out.writeShort(0);
		out.writeInt(bodySize);
		out.writeInt(msg.getId());
		out.writeLong(msg.getCas());
		
		if (msg.hasExtras()) {
			out.writeInt(msg.getFlags());
			out.writeInt(msg.getExpires());
		}
		
		out.writeBytes(key);
		out.writeBytes(body);
	}

}
