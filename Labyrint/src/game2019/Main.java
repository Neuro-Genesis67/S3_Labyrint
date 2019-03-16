package game2019;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
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
	
	
	Socket client_Server;
	static ClientReceiverThread clientReceiverThread;

	public static final int size = 20; 
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<Player>();

	private static Label[][] fields;
	private static TextArea scoreList;
	
	private static  String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		System.out.println("(Main) start()");
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

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			Label lblConnected = new Label();
			
			Button btnConnect = new Button("Connect");
			btnConnect.setOnAction(e -> {
				try {
					connectToServer();
					btnConnect.setDisable(true);
					lblConnected.setText("Connected to server");
				} catch (UnknownHostException e1) {		
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			});
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
			grid.add(btnConnect, 0, 2);
			grid.add(lblConnected, 1, 2);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:  // Get token/Critical section here
					updateGame(me.getName(), me.getCurrentX(), me.getCurrentY(), me.getCurrentX(), me.getCurrentY()-1, me.getDirection(), me.getPoints()); break; 
					
				case DOWN:  
					updateGame(me.getName(), me.getCurrentX(), me.getCurrentY(), me.getCurrentX(), me.getCurrentY()+1, me.getDirection(), me.getPoints()); break;
			
				case LEFT:  
					updateGame(me.getName(), me.getCurrentX(), me.getCurrentY(), me.getCurrentX()-1, me.getCurrentY(), me.getDirection(), me.getPoints()); break;
					
				case RIGHT: 
					updateGame(me.getName(), me.getCurrentX(), me.getCurrentY(), me.getCurrentX()+1, me.getCurrentY(), me.getDirection(), me.getPoints()); 
					break;
			
				
				default: break;
				}
			});
			
            // Setting up standard players
			
			me = new Player("Tom", 5, 13, 5, 13, "up", 0); // name, int currentX, int currentY, int newX, int newY, String direction, int points
			players.add(me);
			fields[5][13].setGraphic(new ImageView(hero_up));

			Player harry = new Player("Bot", 5, 15, 5, 15, "up", 0);
			players.add(harry);
			fields[5][15].setGraphic(new ImageView(hero_up));

			scoreList.setText(getScoreList());
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateGame(String name, int currentX, int currentY, int newX, int newY, String direction, int points) {
		
//		System.out.println("Start of updategame");
//		System.out.println(name);
//		System.out.println(currentX);
//		System.out.println(currentY);
//		System.out.println(newX);
//		System.out.println(newY);
		
	
		
		Player player = null;
		
		// ---------------------------------------Er spilleren i players listen?
		for (Player p : players) {
			
			// Is the player myself?
			if (p.getName().equals(me.getName())) {
				player = me;
				System.out.println("(Main) updateGame() -> " + p.getName() + " My own player has been selected");
				break;
				
		// Is the player someone else in the list?
			} else if (p.getName().equals(name)) {
				player = p;
				System.out.println("(Main) updateGame() -> " + p.getName() + " Someone elses player has been selected");
				break;
			}
		}
		
		// If player is not in playerlist
		if (player == null) {
			System.out.println("(Main) updateGame() -> " + "Player not found - Adding new player to game");
			player = new Player(name, currentX, currentY, newX, newY, direction, points);
			players.add(player);
			if (direction.equals("up")) {
				fields[player.getCurrentX()][player.getCurrentY()].setGraphic(new ImageView(hero_up)); //skal måske være getNewX og newY
			};
			if (direction.equals("down")) {
				fields[player.getCurrentX()][player.getCurrentY()].setGraphic(new ImageView(hero_down));
			};
			if (direction.equals("left")) {
				fields[player.getCurrentX()][player.getCurrentY()].setGraphic(new ImageView(hero_left));
			};
			if (direction.equals("right")) {
				fields[player.getCurrentX()][player.getCurrentY()].setGraphic(new ImageView(hero_right));
			};
			
			scoreList.setText(getScoreList());
		} else {
			
		
		// ----------------------------------------------------------------------
		
		
		// Is x+y a wall?
		if (board[newY].charAt(newX)=='w') { //prøv med ""
			System.out.println("(main) " + player.getName() + " hit a wall");
			player.addPoints(-1);
			scoreList.setText(getScoreList());
		} else {
			
		// Is x+y a player?
			Player otherPlayer = getPlayerAt(newX, newY);
			if (otherPlayer != null) {
				System.out.println("(main) " + player.getName() + " walks into another player");
				player.addPoints(10);
				otherPlayer.addPoints(-10);
				scoreList.setText(getScoreList());
				
			} else {
		
		// move to new location and get 1 point
				System.out.println("(main) " + player.getName() + " walks one step");
				player.addPoints(1);
				fields[currentX][currentY].setGraphic(new ImageView(image_floor));
				
				if (direction.equals("up")) {
					fields[newX][newY].setGraphic(new ImageView(hero_up));
				};
				if (direction.equals("down")) {
					fields[newX][newY].setGraphic(new ImageView(hero_down));
				};
				if (direction.equals("left")) {
					fields[newX][newY].setGraphic(new ImageView(hero_left));
				};
				if (direction.equals("right")) {
					fields[newX][newY].setGraphic(new ImageView(hero_right));
				};
				
				//Set players new position
				player.setCurrentX(newX);
				player.setCurrentY(newY);
				player.setNewX(player.getCurrentX());
				player.setNewY(player.getCurrentY());
				scoreList.setText(getScoreList());
				
//				System.out.println("End of updategame");
//				System.out.println(player.name);
//				System.out.println(player.currentX);
//				System.out.println(player.currentY);
//				System.out.println(player.newX);
//				System.out.println(player.newY);
				
				if (player == me) {
				try {
					sendMove();
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
				
			}
		}
	}
	}

	public static String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getCurrentX()==x && p.getCurrentY()==y) {
				return p;
			}
		}
		return null;
	}

	private void connectToServer() throws UnknownHostException, IOException, InterruptedException {
		clientReceiverThread = new ClientReceiverThread();
		clientReceiverThread.start();
	}
	
	private static void sendMove() throws IOException {
		clientReceiverThread.sendToServer(me.getPlayer() + "\n");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

