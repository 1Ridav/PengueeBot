package bot.penguee;

public class MatrixRectangle {
	final public MatrixPosition p1, p2;

	MatrixRectangle(MatrixPosition p1, MatrixPosition p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public boolean inside(MatrixRectangle other) {
		return false;
	}
	public boolean bounds(MatrixPosition point) {
		if (p1.x > point.x || p2.x < point.x || p1.y > point.y || p2.y < point.y) 
			return false;
		return true;
	}
	public boolean bounds(int x, int y) {
		if (p1.x > x || p2.x < x || p1.y > y || p2.y < y) 
			return false;
		return true;
	}
}
