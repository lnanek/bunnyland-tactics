package name.nanek.gdwprototype.server;

/**
 * A point on the game board.
 * 
 * @author Lance Nanek
 *
 */
class Point {
	
	int row;
	
	int column;
	
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