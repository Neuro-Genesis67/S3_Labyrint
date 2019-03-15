package game2019;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;

public class ClientReceiverThread extends Thread {
	
	int token;
	Main main;
	
	String message = "";
	String name;
	int x;
	int y;
	String direction;
	int points;
	
	Socket client_Server;
	BufferedReader pipeIn;
	DataOutputStream pipeOut;
	String[] parts;
	
	
	public ClientReceiverThread() throws InterruptedException {
		token = 0;
		
		try {
			client_Server = new Socket("192.168.0.100", 5000);
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
	System.out.println("(ClientReceiverThread) run()");
		
		
	try {
		// Send player details when connecting to server
		pipeOut.writeBytes(Main.me.getPlayer() + "\n");
	} catch (IOException e1) {
		e1.printStackTrace();
	}
	
		while(true) {
			try {
				// receives messages from its SCT and implement into the game
				message = pipeIn.readLine();
				parts = message.split("-"); //Skal den initialiseres først?
				
				//Process the string
				name = parts[0];
				x = Integer.parseInt(parts[1]);
				y = Integer.parseInt(parts[2]);
				direction = parts[3];
				points = Integer.parseInt(parts[4]);
				
				Platform.runLater(() -> main.playerMoved(x, y, direction)); //Alter playermoved first
				
				//main.playerMoved(delta_x, delta_y, direction); Probably need to alter the method to fit new Player variables
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	private void getToken() {
		this.token = 1;
	}
	
	private void releaseToken() {
		this.token = 0;
	}
	
	


	public void sendToServer(String playerDetails) throws IOException {
		pipeOut.writeBytes(playerDetails + "\n");
		
	}
	
	

}
