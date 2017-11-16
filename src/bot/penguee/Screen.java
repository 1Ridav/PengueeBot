package bot.penguee;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Screen {
	protected final static Rectangle screenRect = new Rectangle(Toolkit
			.getDefaultToolkit().getScreenSize());
	private BufferedImage image = null;
	protected Frag screenFrag = null;
	private Robot robot;
	private HashMap<Frag, MatrixPosition> cache;

	protected boolean search_in_region = false;
	protected MatrixPosition search_rect_pos1 = new MatrixPosition();
	protected MatrixPosition search_rect_pos2 = new MatrixPosition();

	private boolean useCache = Data.useInternalCache;

	Screen() {
		cache = new HashMap<Frag, MatrixPosition>();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public Screen(boolean useCache) {
		this.useCache = useCache;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	Frag getFrag() {
		return screenFrag;
	}

	public void grab() throws Exception {
			image = robot.createScreenCapture(screenRect);

			if (screenFrag == null) {
				screenFrag = new Frag(image);
			} else {
				// REFRESH MATRIX BUFFER
				screenFrag.getIntRGB(image);
			}
	}

	void grab_rect(int x, int y, int w, int h) throws Exception {
		grab_rect(new Rectangle(x, y, w, h));
	}

	void grab_rect(MatrixPosition p1, MatrixPosition p2) throws Exception {
		grab_rect(new Rectangle(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y));
	}

	void grab_rect(Rectangle rect) throws Exception {

			if (screenFrag == null) {
				grab();
			} else {
				image = robot.createScreenCapture(rect);
				// REFRESH MATRIX BUFFER
				screenFrag.getIntRGB(image, rect.x, rect.y);
			}
	}

	// SETS RECTANGLE WHERE FRAGMENS BEING SEARCHED
	void setSearchRect(MatrixPosition mp1, MatrixPosition mp2) {
		setSearchRect(mp1.x, mp1.y, mp2.x, mp2.y);
	}

	// SETS RECTANGLE WHERE FRAGMENS BEING SEARCHED
	void setSearchRect(int x1, int y1, int x2, int y2) {
		search_rect_pos1.set(x1, y1);
		search_rect_pos2.set(x2, y2);
		search_in_region = true;
	}

	// SETS SEARCH RECTANGLE TO WHOLE SCREEN
	void setSearchRect() {
		search_in_region = false;
	}

	public MatrixPosition find(Frag small) {
		MatrixPosition cachedPos = null;
		if (useCache)
			cachedPos = (MatrixPosition) cache.get(small);
		MatrixPosition mp = null;
		if (cachedPos != null) { // fragment was cached previously, ensure it is
									// still there
			if (search_in_region) {
				if (search_rect_pos1.x <= cachedPos.x
						&& search_rect_pos2.x >= cachedPos.x
						&& search_rect_pos1.y <= cachedPos.y
						&& search_rect_pos2.y >= cachedPos.y) {
					mp = small.findIn(screenFrag, cachedPos.x, cachedPos.y,
							cachedPos.x + 1, cachedPos.y + 1);
				}
			} else {
				mp = small.findIn(screenFrag, cachedPos.x, cachedPos.y,
						cachedPos.x + 1, cachedPos.y + 1);
			}
		}

		if (mp == null) {// cache miss
			int[][] b, s;
			int x_start, y_start, x_stop, y_stop;
			b = screenFrag.getRgbData();
			s = small.getRgbData();
			if (search_in_region) {
				y_start = search_rect_pos1.y;
				x_start = search_rect_pos1.x;
				y_stop = search_rect_pos2.y - s.length + 1;
				x_stop = search_rect_pos2.x - s[0].length;

			} else {
				y_start = 0;
				x_start = 0;
				y_stop = b.length - s.length + 1;
				x_stop = b[0].length - s[0].length;
			}
			mp = small.findIn(screenFrag, x_start, y_start, x_stop, y_stop);
		}

		if (mp != null) {
			if (useCache)
				cache.put(small, new MatrixPosition(mp));
			return mp.add(small.center());//change position to center of fragment and return
		}

		return null;
	}

	public MatrixPosition[] find_all(Frag small) {
		MatrixPosition[] mp = null;
		int[][] big_matrix, small_matrix;
		int x_start, y_start, x_stop, y_stop;
		big_matrix = screenFrag.getRgbData();
		small_matrix = small.getRgbData();
		if (search_in_region) {
			y_start = search_rect_pos1.y;
			x_start = search_rect_pos1.x;
			y_stop = search_rect_pos2.y - small_matrix.length + 1;
			x_stop = search_rect_pos2.x - small_matrix[0].length;

		} else {
			y_start = 0;
			x_start = 0;
			y_stop = big_matrix.length - small_matrix.length + 1;
			x_stop = big_matrix[0].length - small_matrix[0].length;
		}

		mp = small.findAllIn(screenFrag, x_start, y_start, x_stop, y_stop);
		if (mp != null) {//change position to center of fragment
			MatrixPosition center = small.center();
			for (MatrixPosition p: mp)
				p.add(center);
		}

		return mp;
	}

	MatrixPosition find(String name) throws FragmentNotLoadedException {
		return find(name, name);
	}

	MatrixPosition find(String name, String customName)
			throws FragmentNotLoadedException {
		Frag f = (Frag) Data.fragments.get(name);
		if (f != null) {
			MatrixPosition mp = find(f);
			if (mp != null)
				return mp.setName(customName);
			return null;
		} else {
			// System.out.println("CORE: Fragment " + name + " is not loaded");
			throw new FragmentNotLoadedException(name);
		}
	}

	MatrixPosition[] find_all(String name) throws FragmentNotLoadedException {
		return find_all(name, name);
	}

	MatrixPosition[] find_all(String name, String customName)
			throws FragmentNotLoadedException {
		Frag f = (Frag) Data.fragments.get(name);
		if (f != null) {
			MatrixPosition mp[] = find_all(f);
			if (mp != null)
				for (MatrixPosition p : mp)
					p.setName(customName);
			return mp;
		} else {
			// System.out.println("CORE: Fragment " + name + " is not loaded");
			throw new FragmentNotLoadedException(name);
		}
	}

	void loadFragments() {// need to remove soon
		// TODO Auto-generated method stub

	}

}

class FragmentNotLoadedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Parameterless Constructor
	public FragmentNotLoadedException() {
	}

	// Constructor that accepts a message
	public FragmentNotLoadedException(String message) {
		super(message);
	}
}