package bot.penguee;

import java.awt.Point;
/**
 * @deprecated
 * Use Position class, this class is needed for backwards compatibility
 *
 */
public class MatrixPosition extends Position {

	public MatrixPosition(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	public MatrixPosition(int x, int y, String name) {
		super(x, y, name);
		// TODO Auto-generated constructor stub
	}

	public MatrixPosition(Position that) {
		super(that);
		// TODO Auto-generated constructor stub
	}

	public MatrixPosition(Point p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	public MatrixPosition() {
		// TODO Auto-generated constructor stub
	}

}
