package name.nanek.gdwprototype.server.model;

/**
 * A point on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class Point {
	
	public int row;
	
	public int column;
	
	public Point() {};
	
	public Point(Point copy) {
		row = copy.row;
		column = copy.column;
	}
	
	public Point(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
}