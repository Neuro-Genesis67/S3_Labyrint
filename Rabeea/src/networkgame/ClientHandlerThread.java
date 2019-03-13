package networkgame;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandlerThread extends Thread {

    String clientIP;
    Socket clientSocket;
    ArrayList<String> messagesSent = new ArrayList<>();

    public ClientHandlerThread(String clientIP) {
        this.clientIP = clientIP;
    }

    @Override
    public void run() {

        try {
            /*
             * når en klient/spiller kobles på spillet sendes navnet, position, og direction
             * første gang så kører vi i en while løkke hvor placering og point opdateres
             * løbende
             */
            clientSocket = new Socket(clientIP, 6333);

            DataOutputStream outputFirstTime = new DataOutputStream(clientSocket.getOutputStream());

            String outputMessage1 = Main.me.getName() + ", " + Main.me.getXpos() + ", " + Main.me.getYpos() + ", "
                    + Main.me.getDirection();

            outputFirstTime.writeBytes(outputMessage1 + "\n");

            outputFirstTime.close();
            clientSocket.close();

            while (true) {
                try {
                    clientSocket = new Socket(clientIP, 6444);
                    DataOutputStream outputRegularly = new DataOutputStream(clientSocket.getOutputStream());

                    if (!messagesSent.isEmpty()) {
                        String outputMessage = messagesSent.remove(0);

                        outputRegularly.writeBytes(outputMessage + "\n");
                    }

                    outputRegularly.flush();

                    outputRegularly.close();
                    clientSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    


}
