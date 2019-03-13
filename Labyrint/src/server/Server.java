package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	static ServerSocket clientSocket;
	static ServerSocket receiverSocket;
	static Socket connection;
	static ServerClientUpdater scu;
	static List<ServerClientThread> sctList = new ArrayList<>();
	

	
	public static void main(String[] args) {
		System.out.println("(Server) main()");

		scu = new ServerClientUpdater();
		
		try {
			clientSocket = new ServerSocket(5000);
			
		while (true) {
				connection = clientSocket.accept();
				ServerClientThread sct = new ServerClientThread(connection, scu);
				sctList.add(sct);
				System.out.println("(Server) a client has been added to sctList");
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
