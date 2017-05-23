package nettyLearning.chapter2.aio;

public class AIOTimeClient {
	public static void main(String[] args) {
		int port = 8088;
		new Thread(new AsyncTimeClientHandler("127.0.0.1", port), 
				"AIO-TimeClient-001").start();
	}
}
