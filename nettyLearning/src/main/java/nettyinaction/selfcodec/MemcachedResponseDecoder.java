package nettyinaction.selfcodec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public class MemcachedResponseDecoder extends ByteToMessageDecoder {

	private enum State {
		Header, Body
	}

	private State state = State.Header;
	private int totalBodySize;
	private byte magic;
	private byte opCode;
	private short keyLength;
	private byte extraLength;
	private short status;
	private int id;
	private long cas;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//严格按照MemcachedRequest Encoder的格式来Decode
		switch (state) {
			case Header:
				if (in.readableBytes() < 24) {
					return;
				}
				magic = in.readByte();
				opCode = in.readByte();
				keyLength = in.readShort();
				extraLength = in.readByte();
				in.skipBytes(1);
				status = in.readShort();
				totalBodySize = in.readInt();
				id = in.readInt();
				cas = in.readLong();
				
				state = State.Body;
			case Body:
				if (in.readableBytes() < totalBodySize) {
					return;
				}
				int flags = 0;
				int expires = 0;
				int actualBodySize = totalBodySize;
				if (extraLength > 0) {
					flags = in.readInt();
					actualBodySize = actualBodySize - 4;
				}
				if (extraLength > 4) {
					expires = in.readInt();
					actualBodySize = actualBodySize - 4;
				}
				String key = "";
				if (keyLength > 0) {
					ByteBuf keyBytes = in.readBytes(keyLength);
					key = keyBytes.toString(CharsetUtil.UTF_8);
					actualBodySize = actualBodySize - keyLength;
				}
				ByteBuf body = in.readBytes(actualBodySize);
				String data = body.toString(CharsetUtil.UTF_8);
				MemcachedResponse response = new MemcachedResponse(magic, 
						opCode, 
						(byte) 0, 
						status, 
						id, 
						cas, 
						flags, 
						expires, 
						key, 
						data);
				out.add(response);
				state = State.Header;
				break;
			default:
				break;
		}
	}

}
