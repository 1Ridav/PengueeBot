package bot.penguee;

import java.awt.Point;

public class MatrixPosition {

	public int x = -1;
	public int y = -1;
	public String name = null;

	public MatrixPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public MatrixPosition(MatrixPosition mp) {
		this.x = mp.x;
		this.y = mp.y;
	}

	public MatrixPosition(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public MatrixPosition() {

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getName() {
		return name;
	}

	// add coords to current position
	public MatrixPosition add(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	// add coords to current position
	public MatrixPosition add(MatrixPosition that) {
		this.x += that.x;
		this.y += that.y;
		return this;
	}

	// substract coords from current position
	public MatrixPosition sub(int x, int y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	// substract coords from current position
	public MatrixPosition sub(MatrixPosition that) {
		this.x -= that.x;
		this.y -= that.y;
		return this;
	}

	// set coords
	public MatrixPosition set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	// create new position relative to current
	public MatrixPosition relative(int x, int y) {
		return new MatrixPosition(this.x + x, this.y + y);
	}

	// set name to this position
	public MatrixPosition setName(String name) {
		this.name = name;
		return this;
	}

	// same as string method, check for equality between two positions( x y
	// name)
	public boolean equals(MatrixPosition that) {
		return (this.x == that.x && this.y == that.y && this.name != null && this.name
				.equals(that.name));
	}
}