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
		this.direction = direction;
		this.points = points;
		this.newX = newX;
		this.newY = newY;
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
		return  name + "-" + 
				currentX + "-" + 
				currentY + "-" + 
				newX + "-" + 
				newY + "-" + 
				direction + "-" + 
				points;
	}
	@Override
	public String toString() {
		return name+":   "+points;
	}
}
