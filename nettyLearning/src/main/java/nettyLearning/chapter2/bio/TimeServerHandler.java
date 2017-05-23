package nettyLearning.chapter2.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;

/**
 * @date 2017.5.21
 */
public class TimeServerHandler implements Runnable {
	private Socket socket;

	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
			String currentTime = null;
			String body = null;
			while (true) {
				System.out.println("-------------");
				body = in.readLine();
				System.out.println("The time server receive order : " + body);
				if (body == null) {
					break;
				}
				System.out.println("The time server receive order : " + body);
				currentTime = "Query Time Order".equalsIgnoreCase(body) ? LocalDate.now().toString() : "Bad Order";
				out.println(currentTime);
//				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (this.socket != null) {
					this.socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// @Override
	// public void run() {
	// BufferedReader in = null;
	// PrintWriter out = null;
	// try {
	// in = new BufferedReader(new
	// InputStreamReader(this.socket.getInputStream()));
	// out = new PrintWriter(this.socket.getOutputStream(), true);
	// String currentTime = null;
	// String body = null;
	// while (true) {
	// body = in.readLine();
	// if (body == null)
	// break;
	// System.out.println("The time server receive order : " + body);
	// currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
	// ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD
	// ORDER";
	// out.println(currentTime);
	// }
	//
	// } catch (Exception e) {
	// if (in != null) {
	// try {
	// in.close();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// }
	// if (out != null) {
	// out.close();
	// out = null;
	// }
	// if (this.socket != null) {
	// try {
	// this.socket.close();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// this.socket = null;
	// }
	// }
	// }
}
