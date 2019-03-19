package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	static ServerSocket serverSocket;
	static ServerSocket receiverSocket;
	static Socket connection;
	
	static List<ServerClientThread> sctList = new ArrayList<>();
	

	
	public static void main(String[] args) {
		
		try {
			serverSocket = new ServerSocket(5000);
			
		while (true) {
				connection = serverSocket.accept();
				ServerClientThread sct = new ServerClientThread(connection);
				sct.start();
				sctList.add(sct);
				System.out.println("(Server) client added to sctList");
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateClients(String playerDetails) throws IOException {
		System.out.println("(Server) updateClients() -> " + playerDetails);
		for (ServerClientThread sct : sctList) {
			sct.updateGame(playerDetails);
		}
	}
	
	public static List<ServerClientThread> getSctList(){
		return sctList;
	}
	
	
	
	

}
