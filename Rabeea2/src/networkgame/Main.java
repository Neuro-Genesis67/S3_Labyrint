/*
 * Dette spil er beregnet til 2 personer - og kan kun spilles over et lokalt netværk
 * Denne Main klasse køres alene når spillet skal startes
 * Alt kode i dette projekt skal være på begge spilleres computere
 *
 * otherPlayerIP skal være den anden spilleres IPv4 adresse -
 * og den anden spilleres otherPlayerIP skal være denne maskines IPv4 adresse
 * under kommentaren "Setting up standard players" skal "me" initialiseres med navnet på spilleren
 * på denne maskine samt x og y positionen og direction. "otherPlayer" initialiseres med nogle default værdier
 * men opdateres når en spiller kobler sig på - Navnet samt positionen på de 2 spillere må ikke være den samme
 * i ClientHandlerThread er der oprettet 2 clientSockets - portnummeret som de kobles på skal være det samme
 * portnummer som den anden spiller har oprettet de 2 serversockets på i ServerListenerThread og omvendt
 * dvs. hvis den anden spillers øverste serversocket i ServerListenerThread lytter på port 6666
 * skal den øverste clientsocket for denne spiller i ClientHandlerThread lytte på port 6666
 * og hvis den anden spillers nederste serversocket i ServerListenerThread lytter på port 6777
 * skal den nederste clientsocket for denne spiller i ClientHandlerThread også lytte på port 6777
 * det samme skal være gældende for den anden spiller -
 * hans clientsockets skal lytte på de samme porte som denne spilleres serversockets
 */

package networkgame;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private static ClientHandlerThread clientHandlerThread;
    private ServerListenerThread serverListenerThread;

    public static final int size = 20;
    public static final int scene_height = size * 20 + 100;
    public static final int scene_width = size * 20 + 200;

    public static Image image_floor;
    public static Image image_wall;
    public static Image hero_right, hero_left, hero_up, hero_down;

    public static Player me;
    public static Player otherPlayer;
    public static List<Player> players = new ArrayList<>();
    public static String otherPlayerIP = "10.24.4.127";

    private static Label[][] fields;
    public static TextArea scoreList;

    private static GridPane grid;
    private static Scene scene;

    private static String[] board = { // 20x20
            "wwwwwwwwwwwwwwwwwwww", "w        ww        w", "w w  w  www w  w  ww", "w w  w   ww w  w  ww",
            "w  w               w", "w w w w w w w  w  ww", "w w     www w  w  ww", "w w     w w w  w  ww",
            "w   w w  w  w  w   w", "w     w  w  w  w   w", "w ww ww        w  ww", "w  w w    w    w  ww",
            "w        ww w  w  ww", "w         w w  w  ww", "w        w     w  ww", "w  w              ww",
            "w  w www  w w  ww ww", "w w      ww w     ww", "w   w   ww  w      w", "wwwwwwwwwwwwwwwwwwww" };

    // -------------------------------------------
    // | Maze: (0,0) | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1) | scorelist |
    // | | (1,1) |
    // -------------------------------------------

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Netværksspil");
            grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 0, 10));

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            scoreList = new TextArea();

            GridPane boardGrid = new GridPane();

            image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
            image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

            hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
            hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
            hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
            hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

            fields = new Label[20][20];
            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (board[j].charAt(i)) {
                    case 'w':
                        fields[i][j] = new Label("", new ImageView(image_wall));
                        break;
                    case ' ':
                        fields[i][j] = new Label("", new ImageView(image_floor));
                        break;
                    default:
                        throw new Exception("Illegal field value: " + board[j].charAt(i));
                    }
                    boardGrid.add(fields[i][j], i, j);
                }
            }
            scoreList.setEditable(false);

            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(scoreList, 1, 1);

            scene = new Scene(grid, scene_width, scene_height);
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                case UP:
                    playerMoved(me, 0, -1, "up");
                    // if (me.tokenPassing_MutualExclusion.gotTheToken) {
                    // playerMoved(me, 0, -1, "up");
                    // } else {
                    // me.tokenPassing_MutualExclusion.requestToken();
                    // }
                    break;
                case DOWN:
                    playerMoved(me, 0, +1, "down");
                    break;
                case LEFT:
                    playerMoved(me, -1, 0, "left");
                    break;
                case RIGHT:
                    playerMoved(me, +1, 0, "right");
                    break;
                default:
                    break;
                }
            });

            // Setting up standard players
            me = new Player("Rabeea", 9, 4, "up");
            players.add(me);
            fields[me.getXpos()][me.getYpos()].setGraphic(new ImageView(hero_up));

            otherPlayer = new Player("NO PLAYER", 14, 15, "up");
            players.add(otherPlayer);
            fields[otherPlayer.getXpos()][otherPlayer.getYpos()].setGraphic(new ImageView(hero_up));

            scoreList.setText(getScoreList());

            clientHandlerThread = new ClientHandlerThread(otherPlayerIP);
            serverListenerThread = new ServerListenerThread();

            serverListenerThread.start();
            clientHandlerThread.start();

            // et objekt der sørger for gensidig udelukkelse - (player, clock)
            me.tokenPassing_MutualExclusion = new TokenPassing_MutualExclusion(me, 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // den anden spilleres point opdateres ikke i denne metode men opdateres ud fra
    // den besked der modtages når den anden spilleres point ændrer sig
    public static synchronized void playerMoved(Player player, int delta_x, int delta_y, String direction) {
        player.direction = direction;
        int x = player.getXpos(), y = player.getYpos();

        if (board[y + delta_y].charAt(x + delta_x) == 'w') {

            if (!player.equals(otherPlayer)) {
                player.addPoints(-1);
            }

            // ændring af point sendes afsted til den anden spiller
            if (player.equals(me)) {
                clientHandlerThread.messagesSent.add("pointchanged " + player.name + " " + player.getPoint());
            }
        } else {
            Player p = getPlayerAt(x + delta_x, y + delta_y);
            if (p != null) {

                if (!player.equals(otherPlayer)) {
                    player.addPoints(10);
                    p.addPoints(-10);
                }

                // ændring af point sendes afsted til den anden spiller
                if (player.equals(me)) {
                    clientHandlerThread.messagesSent.add("pointchanged " + player.name + " " + player.getPoint());
                    clientHandlerThread.messagesSent.add("pointchanged " + p.name + " " + p.getPoint());
                }
            } else {
                if (!player.equals(otherPlayer)) {
                    player.addPoints(1);
                }

                // ændring af point sendes afsted til den anden spiller
                if (player.equals(me)) {
                    clientHandlerThread.messagesSent.add("pointchanged " + player.name + " " + player.getPoint());
                }

                fields[x][y].setGraphic(new ImageView(image_floor));
                x += delta_x;
                y += delta_y;

                if (direction.equals("right")) {
                    fields[x][y].setGraphic(new ImageView(hero_right));
                }
                if (direction.equals("left")) {
                    fields[x][y].setGraphic(new ImageView(hero_left));
                }
                if (direction.equals("up")) {
                    fields[x][y].setGraphic(new ImageView(hero_up));
                }
                if (direction.equals("down")) {
                    fields[x][y].setGraphic(new ImageView(hero_down));
                }

                player.setXpos(x);
                player.setYpos(y);

                // ændring af placering sendes afsted til den anden spiller
                if (player.equals(me)) {
                    clientHandlerThread.messagesSent.add(
                            "playermoved " + player.getXpos() + " " + player.getYpos() + " " + player.getDirection());
                }
            }
        }
        scoreList.setText(getScoreList());
    }

    public static String getScoreList() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : players) {
            b.append(p + "\r\n");
        }
        return b.toString();
    }

    public static Player getPlayerAt(int x, int y) {
        for (Player p : players) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
