package networkgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;

public class ServerListenerThread extends Thread {

    Socket connectionSocket;
    ServerSocket welcomeSocket;
    ArrayList<String> messagesReceived = new ArrayList<>();

    @Override
    public void run() {

        try {
            /*
             * når en klient/spiller kobles på spillet modtages navnet, position, og
             * direction første gang så kører vi i en while løkke hvor placering og point
             * opdateres løbende
             */
            welcomeSocket = new ServerSocket(6111);
            connectionSocket = welcomeSocket.accept();

            BufferedReader inFromClientFirsttime = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));
            String clientInputFirsttime = inFromClientFirsttime.readLine();

            String[] clientInputParts = clientInputFirsttime.split(", ");

            if (clientInputParts.length == 4) {

                String name = clientInputParts[0]; // name
                String xPos = clientInputParts[1]; // Xpos
                String yPos = clientInputParts[2]; // Ypos
                String direction = clientInputParts[3]; // direction

                Main.otherPlayer.setName(name);
                Main.otherPlayer.setXpos(Integer.parseInt(xPos));
                Main.otherPlayer.setYpos(Integer.parseInt(yPos));
                Main.otherPlayer.setDirection(direction);

                System.out.println("The player: " + Main.otherPlayer.name + " is now connected!");

                Main.scoreList.setText(Main.getScoreList()); // scorelist opdateres
            }

            inFromClientFirsttime.close();
            connectionSocket.close();
            welcomeSocket.close();

            while (true) {
                welcomeSocket = new ServerSocket(6222);
                connectionSocket = welcomeSocket.accept();

                BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(connectionSocket.getInputStream()));

                // der modtages altid 2 beskeder fra en spiller, en besked om point og en besked
                // om positionen
                String clientInputPoint = inFromClient.readLine();
                String clientInputMoved = inFromClient.readLine();

                if (clientInputPoint != null) {
                    messagesReceived.add(clientInputPoint);
                }

                if (clientInputMoved != null) {
                    messagesReceived.add(clientInputMoved);
                }

                if (!messagesReceived.isEmpty()) {

                    for (int i = 0; i < messagesReceived.size(); i++) {

                        if (messagesReceived.get(0).contains("pointchanged")) {

                            String message = messagesReceived.remove(0);
                            String[] messageParts = message.split(" ");

                            System.out.println(message);

                            String name = messageParts[1]; // name
                            String point = messageParts[2]; // point

                            if (name.equals(Main.otherPlayer.getName())) {
                                Main.otherPlayer.setPoint(Integer.parseInt(point));
                            }

                        } else if (messagesReceived.get(0).contains("playermoved")) {

                            String message = messagesReceived.remove(0);
                            String[] messageParts = message.split(" ");

                            System.out.println(message);

                            String xPos = messageParts[1]; // Xpos
                            String yPos = messageParts[2]; // Ypos
                            String direction = messageParts[3]; // direction

                            Platform.runLater(() -> Main.playerMoved(Main.otherPlayer,
                                    // det nye position minus det gamle position - dvs. hvis man fx bevæger sig
                                    // mod venstre er x: -1 og y: 0
                                    // billedet opdateres i playerMoved metoden i Main afhængig af direction
                                    Integer.parseInt(xPos) - Main.otherPlayer.getXpos(),
                                    Integer.parseInt(yPos) - Main.otherPlayer.getYpos(), direction));
                        }
                        Main.scoreList.setText(Main.getScoreList()); // scorelist opdateres
                    }
                }
                inFromClient.close();
                connectionSocket.close();
                welcomeSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
