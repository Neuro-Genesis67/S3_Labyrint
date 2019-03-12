package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

import game2019.Main;

public class ThreadClient extends Thread {

	String message;
	private Socket connection;
	BufferedReader pipeIn;
	DataOutputStream pipeOut;

	public ThreadClient(Socket connection) {
		this.connection = connection; // Tråd <--> Server | Forbindelse |

		// try {
		// pipeIn = new BufferedReader(new
		// InputStreamReader(connection.getInputStream()));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	@Override
	public void run() {
		Server.playerList.add(this);
		System.out.println("The clientThread is running and is now in the list of players");

		// while (true) {
		// // Receive message from server
		// try {
		// message = pipeIn.readLine();
		// System.out.println("ThreadClient: " + message);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// updateGame(message);
		// }
	}

	public void updateGame(String message) {
		Main.updateGame(message);
	}

}

// this.serverSocket = serverSocket;
// Socket connection = serverSocket.accept();
// pipeIn = new BufferedReader(new
// InputStreamReader(connection.getInputStream()));
// pipeOut = new DataOutputStream(connection.getOutputStream());