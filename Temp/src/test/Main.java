package test;

public class Main {

	public static void main(String[] args) {
		
		
		TestPlayer me = new TestPlayer("Tom", 5);
		TestPlayer other = new TestPlayer("OtherPlayer", 1);
		
		System.out.println(me);
		System.out.println(other);
		
		other = me;
		System.out.println(me);
		System.out.println(other);
		other.setPoints(2);
		
		System.out.println(me);
		System.out.println(other);
		

	}

}
