package nettyLearning.chapter2.nio;

public class TimeServer {
	public static void main(String[] args) {
		int port = 8088;
		MultiplexerTimeServer server = new MultiplexerTimeServer(port);
		new Thread(server, "NIO-TimeSerwver-001").start();
	}
}
