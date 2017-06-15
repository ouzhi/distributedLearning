package nettyinaction.websocket;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class SecureChatServer extends ChatServer {
	
	private final SslContext context;
	
	public SecureChatServer(SslContext context) {
		this.context = context;
	}
	
	@Override
	protected ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup) {
		return new SecureChatServerInitializer(channelGroup, context);
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException();
		}
		int port = Integer.parseInt(args[0]);
		try {
			SelfSignedCertificate certificate = new SelfSignedCertificate();
			SslContext context = SslContextBuilder
					.forServer(certificate.certificate(), certificate.privateKey())
					.build();
			final SecureChatServer server = new SecureChatServer(context);
			ChannelFuture future = server.start(new InetSocketAddress(port));
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					server.destroy();
				}
			});
			future.channel().closeFuture().syncUninterruptibly();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
