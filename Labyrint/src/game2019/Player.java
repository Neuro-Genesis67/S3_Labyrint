package game2019;

public class Player {
	String name;
	int currentX;
	int currentY;
	int newX;
	int newY;
	int points;
	String direction;

	public Player(String name, int currentX, int currentY, int newX, int newY, String direction, int points) {
		this.name = name;
		this.currentX = currentX;
		this.currentY = currentY;
		this.newX = newX;
		this.newY = newY;
		this.direction = direction;
		this.points = points;
	}

	public int getCurrentX() {
		return currentX;
	}
	public void setCurrentX(int xpos) {
		this.currentX = xpos;
	}
	public int getCurrentY() {
		return currentY;
	}
	public void setCurrentY(int ypos) {
		this.currentY = ypos;
	}
	public int getNewX() {
		return newX;
	}
	public void setNewX(int newX) {
		this.newX = newX;
	}
	public int getNewY() {
		return newY;
	}
	public void setNewY(int newY) {
		this.newY = newY;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void addPoints(int p) {
		points+=p;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlayer() {
		return  name + "_" + 
				currentX + "_" + 
				currentY + "_" + 
				newX + "_" + 
				newY + "_" + 
				direction + "_" + 
				points;
	}
	@Override
	public String toString() {
		return name+":   "+points;
	}
}
