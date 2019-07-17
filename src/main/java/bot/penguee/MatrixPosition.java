package bot.penguee;

import java.awt.Point;
/**
 * @deprecated
 * Use Position class, this class is needed for backwards compatibility (to be able to run old scripts)
 *
 */
@Deprecated
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
	
	/**
	 * @deprecated
	 * @param x
	 *            x axis coord
	 * @param y
	 *            y axis coord
	 * @return New MatrixPosition with coordinates mp1 + mp2
	 */
	@Deprecated
	public Position relative(int x, int y) {
		return add(x, y);
	}

	/**
	 * @deprecated
	 * @param that
	 *            MatrixPosition which coords will be added to current mp
	 * @return New MatrixPosition with coordinates mp1 + mp2
	 */
	@Deprecated
	public Position relative(Position that) {
		return add(that);
	}

}
