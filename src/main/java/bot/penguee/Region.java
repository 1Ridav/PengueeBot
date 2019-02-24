package bot.penguee;

public class Region {
	final public Position p1, p2;

	public Region(Position p1, Position p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public Region(Position p1, int w, int h) {
		this.p1 = p1;
		this.p2 = p1.add(w, h);
	}

	public Region(int x1, int y1, int x2, int y2) {
		this.p1 = new Position(x1, y1);
		this.p2 = new Position(x2, y2);
	}

	/**
	 * TODO
	 * 
	 * Check if this Region is inbounds of (surrounded by) other MatrixRectangle
	 * object
	 * 
	 * @param other
	 *            Region object
	 * @return true or false
	 */
	public boolean inbounds(Region other) {
		return false;
	}

	/**
	 * Check if this Region bounds that point
	 * 
	 * @param point
	 *            Position object
	 * @return true or false
	 */
	public boolean bounds(Position point) {
		if (p1.x > point.x || p2.x < point.x || p1.y > point.y || p2.y < point.y)
			return false;
		return true;
	}

	/**
	 * Check if this Region bounds that point
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @return true or false
	 */
	public boolean bounds(int x, int y) {
		if (p1.x > x || p2.x < x || p1.y > y || p2.y < y)
			return false;
		return true;
	}
}
