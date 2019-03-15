package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerClientThread extends Thread {

	String message = "";
	Socket server_Client;
	BufferedReader pipeIn;
	DataOutputStream pipeOut;
	Server server;
	
	public ServerClientThread(Socket connection) throws IOException {
		this.server_Client = connection;
		pipeIn = new BufferedReader(new InputStreamReader(server_Client.getInputStream()));
		pipeOut = new DataOutputStream(server_Client.getOutputStream());

	}
	
	@Override
	public void run() {
	System.out.println("(ServerClientThread) run()");
		
		
		while(true) {
			try {
				message = pipeIn.readLine();
				server.updateClients(message);
				System.out.println("(ServerClientThread) scu.updateClients(message)");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void updateGame(String playerDetails) throws IOException {
		pipeOut.writeBytes(playerDetails + "\n");
	}
	
	
	
}
