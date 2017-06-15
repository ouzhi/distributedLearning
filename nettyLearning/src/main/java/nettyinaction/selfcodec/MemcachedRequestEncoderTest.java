package nettyinaction.selfcodec;

import org.junit.Assert;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;

public class MemcachedRequestEncoderTest {
	@Test
	public void testMemcachedRequestEncoder() {
		MemcachedRequest request = new MemcachedRequest(Opcode.SET, "key1", "value1");
		
		EmbeddedChannel channel = new EmbeddedChannel(new MemcachedRequestEncoder());
		channel.writeOutbound(request);
		
		ByteBuf encoded = channel.readOutbound();
		
		Assert.assertNotNull(encoded);
		Assert.assertEquals(request.getMagic(), encoded.readUnsignedByte());
		Assert.assertEquals(request.getOpCode(), encoded.readByte());
		Assert.assertEquals(4, encoded.readShort());  //key.length
		Assert.assertEquals((byte) 0x80, encoded.readByte()); //extra
		Assert.assertEquals((byte) 0, encoded.readByte());
		Assert.assertEquals(0, encoded.readShort());
		Assert.assertEquals(4 + 6 + 8, encoded.readInt());
		Assert.assertEquals(request.getId(), encoded.readInt());
		Assert.assertEquals(request.getCas(), encoded.readLong());
		Assert.assertEquals(request.getFlags(), encoded.readInt());
		Assert.assertEquals(request.getExpires(), encoded.readInt());
		
		byte[] data = new byte[encoded.readableBytes()];
		encoded.readBytes(data);
		Assert.assertArrayEquals((request.getKey() + request.getBody()).getBytes(CharsetUtil.UTF_8),
				data);
		Assert.assertFalse(encoded.isReadable());
		
		Assert.assertFalse(channel.finish());
		Assert.assertNull(channel.readInbound());
	}
}
