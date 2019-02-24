package bot.penguee;

import java.awt.Point;

public class Position {

	public final int x;
	public final int y;
	public String name = null;

	/**
	 * Create a simple MatrixPosition object
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a complete MatrixPosition object
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @param name
	 *            Position name value
	 */
	public Position(int x, int y, String name) {
		this.x = x;
		this.y = y;
		this.name = name;
	}

	/**
	 * Create a clone MatrixPosition object
	 * 
	 * @param that
	 *            MatrixPosition object to be cloned
	 */
	public Position(Position that) {
		this.x = that.x;
		this.y = that.y;
		this.name = that.name;
	}

	public Position(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public Position() {
		x = -1;
		y = -1;
	}

	/**
	 * Get position X axis value
	 * 
	 * @return X axis value
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get position Y axis value
	 * 
	 * @return Y axis value
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get position name value
	 * 
	 * @return name value
	 */
	public String getName() {
		return name;
	}

	// add coords from current position, return new MatrixPosition
	/**
	 * Create a new MatrixPosition object which X and Y axis values generated from
	 * this plus that
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @return New MatrixPosition with position values this.x + x, this.y + y
	 */
	public Position add(int x, int y) {
		return new Position(this.x + x, this.y + y);
	}

	// add coords from current position, return new MatrixPosition
	/**
	 * Create a new MatrixPosition object which X and Y axis values generated from
	 * this plus that
	 * 
	 * @param that
	 *            MatrixPosition object
	 * @return New MatrixPosition with position values this.x + that.x, this.y +
	 *         that.y
	 */
	public Position add(Position that) {
		return new Position(this.x + that.x, this.y + that.y);
	}

	// substract coords from current position, return new MatrixPosition
	/**
	 * Create a new MatrixPosition object which X and Y axis values generated from
	 * this minus that
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @return New MatrixPosition with position values this.x - x, this.y - y
	 */
	public Position sub(int x, int y) {
		return new Position(this.x - x, this.y - y);
	}

	// substract coords from current position, return new MatrixPosition
	/**
	 * Create a new MatrixPosition object which X and Y axis values generated from
	 * this minus that
	 * 
	 * @param that
	 *            MatrixPosition object
	 * @return New MatrixPosition
	 */
	public Position sub(Position that) {
		return new Position(this.x - that.x, this.y - that.y);
	}

	/**
	 * @deprecated
	 * @param x
	 *            x axis coord
	 * @param y
	 *            y axis coord
	 * @return New MatrixPosition with coordinates mp1 + mp2
	 */
	public Position relative(int x, int y) {
		return add(x, y);
	}

	/**
	 * @deprecated
	 * @param that
	 *            MatrixPosition which coords will be added to current mp
	 * @return New MatrixPosition with coordinates mp1 + mp2
	 */
	public Position relative(Position that) {
		return add(that);
	}

	/**
	 * Check if this matrix position is inbounds of Region
	 * 
	 * @param rect
	 *            Region
	 * @return true or false
	 */
	public boolean inside(Region rect) {
		return rect.bounds(this);
	}

	// set name to this position
	/**
	 * Set custom name to position object
	 * 
	 * @param name
	 *            Your custom name
	 * @return Current MatrixPosition
	 */
	public Position setName(String name) {
		this.name = name;
		return this;
	}

	// same as string method, check for equality between two positions( x y
	// name)
	/**
	 * Check if two position objects are identical
	 * 
	 * @param that
	 *            Another MatrixPosition object you are trying to compare
	 * @return true or false
	 */
	public boolean equals(Position that) {
		return (this.x == that.x && this.y == that.y
				&& (this.name == that.name || this.name != null && this.name.equals(that.name)));
	}
}