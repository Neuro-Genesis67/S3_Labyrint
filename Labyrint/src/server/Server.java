package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server extends Thread {
	// HashSet cannot contain duplicate values. -Does this work on threads?
	public static Set<ThreadClient> playerList = new HashSet<>();
	// static DataOutputStream pipeOut;
	static BufferedReader inFromClient;
	static ThreadClient tc;
	static ThreadClientListener listener;
	static ServerSocket serverSocket;

	public static void main(String[] args) throws Exception, IOException {
		serverSocket = new ServerSocket(6000);

		while (true) {
			acceptClient();
		}
	}

	public static void acceptClient() throws Exception {
		Socket server_client = serverSocket.accept();
		tc = new ThreadClient(server_client);
		listener = new ThreadClientListener(server_client);
		listener.start();
		tc.start();
	}
}
