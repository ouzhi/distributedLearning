package nettyLearning.chapter2.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @date 2017.5.21
 */
public class TimeClient {
	public static void main(String[] args) {
		int port = 8088;
		try(Socket socket = new Socket("127.0.0.1", port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			out.println("Query Time Order");
			out.println("Query Time Order1");
			System.out.println("send order to server succed.");
			String resp = in.readLine();
			System.out.println("Now is : " + resp);
			resp = in.readLine();
			System.out.println("Now is : " + resp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
