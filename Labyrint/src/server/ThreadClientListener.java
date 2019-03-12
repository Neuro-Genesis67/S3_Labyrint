package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadClientListener extends Thread {

	Socket connection;
	BufferedReader pipeIn;
	String message = "";

	public ThreadClientListener(Socket connection) throws Exception {
		this.connection = connection;
		pipeIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	}

	@Override
	public void run() {
		System.out.println("listener is running");
		while (true) {
			try {
				updateClients();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void updateClients() throws Exception {
		message = pipeIn.readLine();
		System.out.println("TCListener |updateClients()|: " + message);
		for (ThreadClient tc : Server.playerList) {
			tc.updateGame(message);
		}
	}
}
