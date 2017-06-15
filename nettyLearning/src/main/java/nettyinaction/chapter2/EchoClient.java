package nettyinaction.chapter2;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
	private String host;
	private int port;
	
	public EchoClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void start() throws Exception {
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
				.remoteAddress(new InetSocketAddress(host, port))
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addFirst(new EchoClientHandler());
						ch.pipeline().addLast(new EchoClientHandler());
						ch.pipeline().addFirst(new EchoClientHandler());
					}
				});
			
			ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8088;
		if(args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		new EchoClient("127.0.0.1", port).start();
	}
	
}
