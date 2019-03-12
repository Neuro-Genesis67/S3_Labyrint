package game2019;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.application.PlatformImpl;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

	public static final int size = 20;
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right, hero_left, hero_up, hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<Player>();

	private static Label[][] fields;
	private TextArea scoreList;

	Socket client_server;
	// BufferedReader pipeIn;
	DataOutputStream outToServer;

	private String[] board = { // 20x20
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
			GridPane grid = new GridPane();
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

			// Generating labyrinth
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

			Button btnConnect = new Button("Connect");
			btnConnect.setOnAction(e -> {

				try {
					client_server = new Socket("192.168.1.103", 6000);
					outToServer = new DataOutputStream(client_server.getOutputStream());
					notifyServer();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			});

			grid.add(mazeLabel, 0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid, 0, 1);
			grid.add(scoreList, 1, 1);
			grid.add(btnConnect, 1, 2);

			Scene scene = new Scene(grid, scene_width, scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:
					playerMoved(0, -1, "hero_up");
					try {
						notifyServer();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case DOWN:
					playerMoved(0, +1, "hero_down");
					try {
						notifyServer();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case LEFT:
					playerMoved(-1, 0, "hero_left");
					try {
						notifyServer();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case RIGHT:
					playerMoved(+1, 0, "hero_right");
					try {
						notifyServer();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				default:
					break;
				}
			});

			// Setting up standard players

			me = new Player("Orville", 9, 4, "hero_up");
			players.add(me);
			fields[9][4].setGraphic(new ImageView(hero_up));

			// Test player:
			Player harry = new Player("Harry", 14, 15, "hero_left");
			players.add(harry);
			fields[14][15].setGraphic(new ImageView(hero_left));

			scoreList.setText(getScoreList());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void playerMoved(int delta_x, int delta_y, String direction) {
		me.direction = direction;
		int x = me.getXpos(), y = me.getYpos();

		// Hvis gå ind i væg
		if (board[y + delta_y].charAt(x + delta_x) == 'w') {
			me.addPoints(-1);
		} else {
			// Hvis gå ind i spiller
			Player p = getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				me.addPoints(10);
				p.addPoints(-10);
			} else {
				// Hvis gå ind i gulv
				me.addPoints(1);

				fields[x][y].setGraphic(new ImageView(image_floor));
				x += delta_x;
				y += delta_y;

				if (direction.equals("hero_right")) {
					fields[x][y].setGraphic(new ImageView(hero_right));
				}
				;
				if (direction.equals("hero_left")) {
					fields[x][y].setGraphic(new ImageView(hero_left));
				}
				;
				if (direction.equals("hero_up")) {
					fields[x][y].setGraphic(new ImageView(hero_up));
				}
				;
				if (direction.equals("hero_down")) {
					fields[x][y].setGraphic(new ImageView(hero_down));
				}
				;

				me.setXpos(x);
				me.setYpos(y);
			}
		}
		scoreList.setText(getScoreList());
	}

	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		return b.toString();
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

	public static void updateGame(String message) {
		String[] parts = message.split("-");
		String name = parts[0];
		int x = Integer.parseInt(parts[1]);
		int y = Integer.parseInt(parts[2]);
		String direction = parts[3];
		int point = Integer.parseInt(parts[4]);

		Boolean found = false;

		for (Player p : players) {
			// If player exists already - Change his details

			if (p.getName().equals(name)) {
				System.out.println("updateGame() - Player already in list");
				p.setXpos(x);
				p.setYpos(y);
				p.setDirection(direction);
				p.setPoint(point);
				// ---------------------------------------------------
				PlatformImpl.startup(() -> {
				});
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						fields[x][y].setGraphic(new ImageView(direction));
					}
				});
				// ---------------------------------------------------
				found = true;
				System.out.println("updateGame() - Changed a players details");
			}
		}
		// If the player doesn't exist - Create a new player and add him to list
		if (!found) {
			System.out.println("updateGame() - Player not in list");
			Player newPlayer = new Player(name, x, y, direction);
			newPlayer.setPoint(point);
			players.add(newPlayer);

			// ---------------------------------------------------
			PlatformImpl.startup(() -> {
			});
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					fields[x][y].setGraphic(new ImageView(direction));
				}
			});
			// ---------------------------------------------------
			System.out.println("updateGame() - Player has been added to list");
		}
		found = false;

	}

	public void notifyServer() throws IOException {
		// Critical section here?
		outToServer.writeBytes(me.getDetails() + "\n");
	}

	public static void main(String[] args) {
		launch(args);
	}

}
