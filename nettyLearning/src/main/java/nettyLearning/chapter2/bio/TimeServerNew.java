package nettyLearning.chapter2.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServerNew {
	public static void main(String[] args) {
		int port = 8088;
		try(ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("The time server is start in port : " + port);
			Socket socket = null;
			TimeServerHandlerExecutePool executePool = new TimeServerHandlerExecutePool(50, 1000);
			while(true) {
				socket = serverSocket.accept();
				System.out.println("A new request have arrived!");
				executePool.execute(new TimeServerHandler(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
