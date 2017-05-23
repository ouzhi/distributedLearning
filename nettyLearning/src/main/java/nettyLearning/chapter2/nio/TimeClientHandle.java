package nettyLearning.chapter2.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {
	
	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;
	
	public TimeClientHandle(String host, int port) {
		this.host = host == null ? "localhost" : host;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void handleInput(SelectionKey key) throws ClosedChannelException, IOException {
		if(key.isValid()) {
			SocketChannel channel = (SocketChannel) key.channel();
			if(key.isConnectable()) {
				if(channel.finishConnect()) {
					channel.register(selector, SelectionKey.OP_READ);
					doWrite(socketChannel);
				} else {
					System.exit(1);
				}
			}
			if(key.isReadable()) {
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = channel.read(readBuffer);
				if(readBytes > 0) {
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("Now is : " + body);
					this.stop = true;
				} else if(readBytes < 0) {
					key.cancel();
					channel.close();
				}
			}
		}
	}
	
	private void doConnect() throws IOException {
		if(socketChannel.connect(new InetSocketAddress(host, port))) {
			System.out.println("11");
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		} else {
			System.out.println("22");
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	
	private void doWrite(SocketChannel channel) throws IOException {
		byte[] request = "Query Time Order".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(request.length);
		writeBuffer.put(request);
		writeBuffer.flip();
		while(writeBuffer.hasRemaining()) {
			channel.write(writeBuffer);
		}
		System.out.println("Send order to server succed!");
	}
	
	@Override
	public void run() {
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while(!stop) {
			try {
				int readyCount = selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				if(readyCount == 0) continue;
				for(Iterator<SelectionKey> it = selectedKeys.iterator(); it.hasNext(); ) {
					SelectionKey key = it.next();
					try {
						handleInput(key);
					} catch(IOException e) {
						if(key != null) {
							key.cancel();
							if(key.channel() != null) {
								key.channel().close();
							}
						}
					}
					it.remove();
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if(selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
