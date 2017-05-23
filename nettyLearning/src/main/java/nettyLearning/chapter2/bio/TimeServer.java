package nettyLearning.chapter2.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @date 2017.5.21
 */
public class TimeServer {
	public static void main(String[] args) {
		int port = 8088;
		try(ServerSocket serverSocket = new ServerSocket(port);) {
			System.out.println("The time server is start in port : " + port);
			Socket socket = null;
			while(true) {
				socket = serverSocket.accept();
				System.out.println("A new request have arrived!");
				new Thread(new TimeServerHandler(socket)).start();;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
