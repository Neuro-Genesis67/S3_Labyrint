package game2019;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientReceiverThread extends Thread {
	
	int token;
	Main main;
	
	String message = "";
	String name;
	int x;
	int y;
	String direction;
	String points;
	
	Socket client_Server;
	BufferedReader pipeIn;
	DataOutputStream pipeOut;
	String[] parts;
	
	
	public ClientReceiverThread() throws InterruptedException {
		token = 0;
		
		
		try {
			Thread.sleep(200);
			client_Server = new Socket("192.168.0.100", 5000);
			pipeIn = new BufferedReader(new InputStreamReader(client_Server.getInputStream()));
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
		pipeOut = new DataOutputStream(client_Server.getOutputStream());
		pipeOut.writeBytes(Main.me.getPlayer() + "\n");
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
		while(true) {
			try {
				message = pipeIn.readLine();
				parts = message.split("-"); //Skal den initialiseres først?
				
				//Process the string
				
//				main.playerMoved(delta_x, delta_y, direction); Probably need to alter the method to fit new Player variables
				
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
	
	public void sendToSCT() throws IOException {
		pipeOut.writeBytes(Main.me.getPlayer());
	}
	
	

}
