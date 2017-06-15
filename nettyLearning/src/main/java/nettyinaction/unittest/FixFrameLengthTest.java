package nettyinaction.unittest;

import org.junit.Assert;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

public class FixFrameLengthTest {
	@Test
	public void testFixLengthFrameDecode() {
		ByteBuf buf = Unpooled.buffer(256);
		for (int i = 0; i < 9; i++) {
			buf.writeByte(i);
		}
		ByteBuf input = buf.copy();

		EmbeddedChannel embeddedChannel = new EmbeddedChannel(new FixLengthFrameDecoder(3));
		
		Assert.assertFalse(embeddedChannel.writeInbound(input.readBytes(2)));
		Assert.assertTrue(embeddedChannel.writeInbound(input.readBytes(7)));
		
		Assert.assertTrue(embeddedChannel.finish());
		ByteBuf readBuf = embeddedChannel.readInbound();
		Assert.assertEquals(buf.readSlice(3), readBuf);
		readBuf.release();
		
		readBuf = embeddedChannel.readInbound();
		Assert.assertEquals(buf.readSlice(3), readBuf);
		readBuf.release();
		
		readBuf = embeddedChannel.readInbound();
		Assert.assertEquals(buf.readSlice(3), readBuf);
		readBuf.release();
		
		readBuf = embeddedChannel.readInbound();
		Assert.assertNull(readBuf);
	}
}
