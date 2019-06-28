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
	 * Check if this Region bounds (surround) other Region
	 * object
	 * 
	 * @param that
	 *            Region object
	 * @return true or false
	 */
	public boolean bounds(Region that) {
		return !(this.p1.x > that.p1.x || this.p1.y > that.p1.y || this.p2.x < that.p2.x || this.p2.y < that.p2.y);
	}

	/**
	 * Check if this Region bounds that point
	 * 
	 * @param point
	 *            Position object
	 * @return true or false
	 */
	public boolean bounds(Position point) {
		return bounds(point.x, point.y);
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
		if (this.p1.x > x || this.p2.x < x || this.p1.y > y || this.p2.y < y)
			return false;
		return true;
	}
	
	public boolean equals(Region that) { //check x and y values only, position names are not needed at all
		return !(this.p1.x != that.p1.x || this.p1.y != that.p1.y || this.p2.x != that.p2.x || this.p2.y != that.p2.y); 
	}
}
