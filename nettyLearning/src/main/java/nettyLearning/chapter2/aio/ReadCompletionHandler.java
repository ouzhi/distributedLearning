package nettyLearning.chapter2.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.time.LocalDateTime;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel channel) {
		if(this.channel == null) {
			this.channel = channel;
		}
	}
	
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		try {
			String request = new String(body, "utf-8");
			System.out.println("The time server receive order : " + request);
			String currentTime = "Query Time Order".equalsIgnoreCase(request) ?
					LocalDateTime.now().toString() : "Bad Order";
			doWrite(currentTime);
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void doWrite(String response) {
		if(response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					if(attachment.hasRemaining()) {
						channel.write(attachment, attachment, this);
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
 	
}
