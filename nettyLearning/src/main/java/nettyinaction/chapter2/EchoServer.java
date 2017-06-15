package nettyinaction.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
	public static void main(String[] args) throws Exception {
		int port = 8088;
		if (args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("The input target error!" + args[0]);
			}
		}
		new EchoServer().start(port);
	}

	public void start(int port) throws Exception {
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(group).channel(NioServerSocketChannel.class).localAddress(port)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline().addLast(new EchoServerHandler());
						}
					});

			ChannelFuture f = b.bind().sync();
			System.out.println(
					EchoServer.class.getName() + " start and listener in address : " + f.channel().localAddress());
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
}
