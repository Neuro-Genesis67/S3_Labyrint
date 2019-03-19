package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import game2019.Main;
import game2019.Player;

public class ServerClientThread extends Thread {

	String message = "";
	Socket server_Client;
	BufferedReader pipeIn;
	DataOutputStream pipeOut;
	
	public ServerClientThread(Socket connection) throws IOException {
		this.server_Client = connection;
		pipeIn = new BufferedReader(new InputStreamReader(server_Client.getInputStream()));
		pipeOut = new DataOutputStream(server_Client.getOutputStream());

	}
	
	@Override
	public void run() {
		
		
		while(true) {
			try {
				message = pipeIn.readLine();
				while (message.length() < 5) {
					
				}
				Server.updateClients(message);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void updateGame(String playerDetails) throws IOException {
		pipeOut.writeBytes(playerDetails + "\n");
	}
	
	public Player getPlayer() {
		System.out.println("(ServerClientThread) getPlayer()");
		return Main.me;
	}
	
	
	
}
