package name.nanek.gdwprototype.server;

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