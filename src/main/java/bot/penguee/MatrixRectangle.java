package bot.penguee;

public class MatrixRectangle {
	final public MatrixPosition p1, p2;

	MatrixRectangle(MatrixPosition p1, MatrixPosition p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	/**
	 * 
	 * Check if this MatrixRectangle is inbounds of (surrounded by) other MatrixRectangle object
	 * @param other MatrixRectangle object
	 * @return true or false
	 */
	public boolean inbounds(MatrixRectangle other) {
		return false;
	}
	/**
	 * Check if this MatrixRectangle bounds that point
	 * @param point MatrixPosition object
	 * @return true or false
	 */
	public boolean bounds(MatrixPosition point) {
		if (p1.x > point.x || p2.x < point.x || p1.y > point.y || p2.y < point.y) 
			return false;
		return true;
	}
	/**
	 * Check if this MatrixRectangle bounds that point
	 * @param x X axis value
	 * @param y Y axis value
	 * @return true or false
	 */
	public boolean bounds(int x, int y) {
		if (p1.x > x || p2.x < x || p1.y > y || p2.y < y) 
			return false;
		return true;
	}
}
