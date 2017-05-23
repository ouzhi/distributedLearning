package nettyLearning.chapter2.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel serverChannel;
	private volatile boolean stop;
	
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress("localhost", port), 1024);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port : " + port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void stop() {
		this.stop = true;
	}
	
	@Override
	public void run() {
		while(!stop){
			try {
				int readyCount = selector.select(1000);
				if(readyCount == 0) continue;
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				for(Iterator<SelectionKey> iterator = selectedKeys.iterator(); iterator.hasNext();) {
					SelectionKey key = iterator.next();
					try {
						handleInput(key);
					} catch(Exception e) {
						if(key != null) {
							key.cancel();
							if(key.channel() != null) {
								key.channel().close();
							}
						}
					}
					iterator.remove();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	private void handleInput(SelectionKey key) throws IOException {
		if(key.isValid()) {
			if(key.isAcceptable()) {
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()) {
				SocketChannel socketChannel = (SocketChannel) key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int readBytes = socketChannel.read(buffer);
				if(readBytes > 0) {
					buffer.flip();
					byte[] bytes = new byte[buffer.remaining()];
					buffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order : " + body);
					String currentTime = "Query Time Order".equalsIgnoreCase(body) ?
							LocalDateTime.now().toString() : "Bad Order";
					doWrite(socketChannel, currentTime);
				} else if(readBytes < 0) {
					key.cancel();
					socketChannel.close();
				} else {
					assert readBytes == 0; //ignore
				}
			}
		}
	}
	
	private void doWrite(SocketChannel socketChannel, String response) throws IOException {
		if(response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			//写半包问题，异步阻塞情况要注意半包问题
			while(writeBuffer.hasRemaining()) {
				socketChannel.write(writeBuffer);
			}
		}
	}
}











