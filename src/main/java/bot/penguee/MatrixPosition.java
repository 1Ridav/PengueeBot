package bot.penguee;

import java.awt.Point;

public class MatrixPosition {

	public final int x;
	public final int y;
	public String name = null;

	public MatrixPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public MatrixPosition(int x, int y, String name) {
		this.x = x;
		this.y = y;
		this.name = name;
	}

	public MatrixPosition(MatrixPosition that) {
		this.x = that.x;
		this.y = that.y;
		this.name = that.name;
	}

	public MatrixPosition(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public MatrixPosition() {
		x = -1;
		y = -1;
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

	// add coords from current position, return new MatrixPosition
	public MatrixPosition add(int x, int y) {
		return new MatrixPosition(this.x + x, this.y + y);
	}

	// add coords from current position, return new MatrixPosition
	public MatrixPosition add(MatrixPosition that) {
		return new MatrixPosition(this.x + that.x, this.y + that.y);
	}

	// substract coords from current position, return new MatrixPosition
	public MatrixPosition sub(int x, int y) {
		return new MatrixPosition(this.x - x, this.y - y);
	}

	// substract coords from current position, return new MatrixPosition
	public MatrixPosition sub(MatrixPosition that) {
		return new MatrixPosition(this.x - that.x, this.y - that.y);
	}
	
	public MatrixPosition relative(int x, int y) {
		return add(x, y);
	}
	
	public MatrixPosition relative(MatrixPosition that) {
		return add(that);
	}
	
	public boolean inside(MatrixRectangle rect) {
		return rect.bounds(this);
	}
	

	// set name to this position
	public MatrixPosition setName(String name) {
		this.name = name;
		return this;
	}
	
	// same as string method, check for equality between two positions( x y
	// name)
	public boolean equals(MatrixPosition that) {
		return (this.x == that.x && this.y == that.y && this.name != null && this.name.equals(that.name));
	}
}