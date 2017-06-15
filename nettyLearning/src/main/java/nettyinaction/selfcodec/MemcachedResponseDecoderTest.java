package nettyinaction.selfcodec;

import org.junit.Assert;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;

public class MemcachedResponseDecoderTest {
	@Test
	public void testMemcachedResponseDecoder() {
		EmbeddedChannel channel = new EmbeddedChannel(new MemcachedResponseDecoder());
		
		byte magic = 1;
		byte opCode = Opcode.SET;
		
		byte[] key = "key1".getBytes(CharsetUtil.UTF_8);
		byte[] body = "value1".getBytes(CharsetUtil.UTF_8);
		int id = (int) System.currentTimeMillis();
		long cas = System.currentTimeMillis();
		
		ByteBuf buf = Unpooled.buffer(1024);
		buf.writeByte(magic);
		buf.writeByte(opCode);
		buf.writeShort(key.length);
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeShort(Status.KEY_EXISTS);
		buf.writeInt(body.length + key.length);
		buf.writeInt(id);
		buf.writeLong(cas);
		buf.writeBytes(key);
		buf.writeBytes(body);

		Assert.assertTrue(channel.writeInbound(buf));
		MemcachedResponse response = channel.readInbound();
		assertResponse(response, magic, opCode, Status.KEY_EXISTS, 0, 0,
				id, cas, key, body);
	}
	
	@Test
	public void testMemcachedResponseDecoderFragments() {
		EmbeddedChannel channel = new EmbeddedChannel(new MemcachedResponseDecoder());
		
		byte magic = 1;
		byte opCode = Opcode.SET;
		
		byte[] key = "key1".getBytes(CharsetUtil.UTF_8);
		byte[] body = "value1".getBytes(CharsetUtil.UTF_8);
		int id = (int) System.currentTimeMillis();
		long cas = System.currentTimeMillis();
		
		ByteBuf buf = Unpooled.buffer(1024);
		buf.writeByte(magic);
		buf.writeByte(opCode);
		buf.writeShort(key.length);
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeShort(Status.KEY_EXISTS);
		buf.writeInt(body.length + key.length);
		buf.writeInt(id);
		buf.writeLong(cas);
		buf.writeBytes(key);
		buf.writeBytes(body);

		
		ByteBuf fragment1 = buf.readBytes(8);
		ByteBuf fragment2 = buf.readBytes(24);
		ByteBuf fragment3 = buf;
		
		Assert.assertFalse(channel.writeInbound(fragment1));
		Assert.assertFalse(channel.writeInbound(fragment2));
		Assert.assertTrue(channel.writeInbound(fragment3));
		
		MemcachedResponse response = channel.readInbound();
		assertResponse(response, magic, opCode, Status.KEY_EXISTS, 0, 0,
				id, cas, key, body);
	}
	
	private static void assertResponse(MemcachedResponse response, byte magic,
			byte opCode, short status, int expires, int flags, int id,
			long cas, byte[] key, byte[] body) {
		Assert.assertEquals(magic, response.getMagic());
		Assert.assertArrayEquals(key, response.getKey().getBytes(CharsetUtil.UTF_8));
		Assert.assertEquals(opCode, response.getOpCode());
		Assert.assertEquals(status, response.getStatus());
		Assert.assertEquals(cas, response.getCas());
		Assert.assertEquals(expires, response.getExpires());
		Assert.assertEquals(flags, response.getFlags());
		Assert.assertArrayEquals(body, response.getData().getBytes(CharsetUtil.UTF_8));
		Assert.assertEquals(id, response.getId());
	}
}
