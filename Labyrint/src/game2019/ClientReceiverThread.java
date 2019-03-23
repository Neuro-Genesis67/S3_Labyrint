package game2019;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;
import server.Server;
import server.ServerClientThread;

public class ClientReceiverThread extends Thread {
	
	
	String message = "";
	
	String name;
	int currentX;
	int currentY;
	int newX;
	int newY;
	int points;
	String direction;
	
	Socket client_Server;
	BufferedReader pipeIn;
	DataOutputStream pipeOut;
	String[] parts;
	
	public ClientReceiverThread() throws InterruptedException {
		
		try {
			client_Server = new Socket("192.168.0.102", 5000);
			pipeIn = new BufferedReader(new InputStreamReader(client_Server.getInputStream()));
			pipeOut = new DataOutputStream(client_Server.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
	
	// When connecting, get other player's info
	for (ServerClientThread sct : Server.getSctList()) {
		System.out.println("(ClientReceiverThread) Getting other players' info");
		parts = sct.getPlayer().getPlayer().split("_");
		name = parts[0];
		currentX = Integer.parseInt(parts[1]);
		currentY = Integer.parseInt(parts[2]);
		newX = Integer.parseInt(parts[3]);
		newY = Integer.parseInt(parts[4]);
		direction = parts[5];
		points = Integer.parseInt(parts[6]);
		
		
		
		parts = null;
		
		
		
		Main.updateGame(name, currentX, currentY, newX, newY, direction, points);
	}

	try {
		pipeOut.writeBytes(Main.me.getPlayer() + "\n");
	} catch (IOException e1) {
		e1.printStackTrace();
	}
		while(true) {
			try {
				// receives messages from its SCT and implement into the game
				message = pipeIn.readLine();
			while (message.length() < 5) {
				
			}
				
					System.out.println("(ClientReceiverThread) received: " + message);
					parts = null;
					parts = message.split("_"); //Skal den initialiseres først?
					
					//Process the string
					name = parts[0];
					currentX = Integer.parseInt(parts[1]);
					currentY = Integer.parseInt(parts[2]);
					newX = Integer.parseInt(parts[3]);
					newY = Integer.parseInt(parts[4]);
					direction = parts[5];
					points = Integer.parseInt(parts[6]);
					
					
			            	
					Platform.runLater(() -> {    
					
					Main.updateGame(name, currentX, currentY, newX, newY, direction, points); 
					        	  
					}); 
				
			

				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	


	public void updatePlayers(String playerDetails) throws IOException {
		System.out.println("(ClientReceiverThread) sendToServer() -> " + playerDetails);
		pipeOut.writeBytes(playerDetails + "\n");
	}
	
	public void updateMain(String name, int currentX, int currentY, int newX, int newY, String direction, int points) {
		Main.updateGame(name, currentX, currentY, newX, newY, direction, points);
	}
	
	

}
