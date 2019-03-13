package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerClientThread extends Thread {

	String message = "";
	Socket server_Client;
	ServerClientUpdater scu;
	BufferedReader pipeIn;
	
	public ServerClientThread(Socket connection, ServerClientUpdater scu) throws IOException {
		this.server_Client = connection;
		this.scu = scu;
		pipeIn = new BufferedReader(new InputStreamReader(server_Client.getInputStream()));

	}
	
	@Override
	public void run() {
	System.out.println("(ServerClientThread) run()");
		
		
		while(true) {
			try {
				message = pipeIn.readLine();
				scu.updateClients(message);
				System.out.println("(ServerClientThread) scu.updateClients(message)");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
}
