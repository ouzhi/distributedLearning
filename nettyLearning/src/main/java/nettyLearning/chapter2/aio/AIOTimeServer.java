package nettyLearning.chapter2.aio;

public class AIOTimeServer {
	public static void main(String[] args) {
		int port = 8088;
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
		new Thread(timeServer, "AIO-TimeServer-001").start();
	}
}
