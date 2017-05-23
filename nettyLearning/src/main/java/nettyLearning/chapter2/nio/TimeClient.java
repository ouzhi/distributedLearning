package nettyLearning.chapter2.nio;

public class TimeClient {
	public static void main(String[] args) {
		int port = 8088;
		new Thread(new TimeClientHandle("127.0.0.1", port)).start();
	}
}
