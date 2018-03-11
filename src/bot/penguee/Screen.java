package bot.penguee;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Screen {
	protected final Rectangle screenRect = new Rectangle(Toolkit
			.getDefaultToolkit().getScreenSize());
	private BufferedImage image = null;
	protected Frag screenFrag = null;
	private Robot robot;
	private HashMap<Frag, MatrixPosition> cache;

	protected boolean searchInRegion = false;
	protected MatrixPosition searchRectPos1 = null;
	protected MatrixPosition searchRectPos2 = null;

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
		searchRectPos1 = new MatrixPosition(x1, y1);
		searchRectPos2 = new MatrixPosition(x2, y2);
		searchInRegion = true;
	}

	// SETS SEARCH RECTANGLE TO WHOLE SCREEN
	void setSearchRect() {
		searchInRegion = false;
	}

	public MatrixPosition find(Frag smallFragment) {
		MatrixPosition cachedPos = null;
		if (useCache)
			cachedPos = (MatrixPosition) cache.get(smallFragment);
		MatrixPosition mp = null;
		if (cachedPos != null) { // fragment was cached previously, ensure it is
									// still there
			if (searchInRegion) {
				if (searchRectPos1.x <= cachedPos.x
						&& searchRectPos2.x >= cachedPos.x
						&& searchRectPos1.y <= cachedPos.y
						&& searchRectPos2.y >= cachedPos.y) {
					mp = smallFragment.findIn(screenFrag, cachedPos.x, cachedPos.y,
							cachedPos.x + 1, cachedPos.y + 1);
				}
			} else {
				mp = smallFragment.findIn(screenFrag, cachedPos.x, cachedPos.y,
						cachedPos.x + 1, cachedPos.y + 1);
			}
		}

		if (mp == null) {// cache miss
			int x_start, y_start, x_stop, y_stop;
			if (searchInRegion) {
				y_start = searchRectPos1.y;
				x_start = searchRectPos1.x;
				y_stop = searchRectPos2.y - smallFragment.getHeight() + 1;
				x_stop = searchRectPos2.x - smallFragment.getWidth();

			} else {
				y_start = 0;
				x_start = 0;
				y_stop = screenFrag.getHeight() - smallFragment.getHeight() + 1;
				x_stop = screenFrag.getWidth() - smallFragment.getWidth();
			}
			mp = smallFragment.findIn(screenFrag, x_start, y_start, x_stop, y_stop);
		}

		if (mp != null) {
			if (useCache)
				cache.put(smallFragment, mp);
			return mp.add(smallFragment.center());//change position to center of fragment and return
		}
		return null;
	}

	public MatrixPosition[] find_all(Frag smallFragment) {
		MatrixPosition[] mp = null;
		int x_start, y_start, x_stop, y_stop;
		if (searchInRegion) {
			y_start = searchRectPos1.y;
			x_start = searchRectPos1.x;
			y_stop = searchRectPos2.y - smallFragment.getHeight() + 1;
			x_stop = searchRectPos2.x - smallFragment.getWidth();

		} else {
			y_start = 0;
			x_start = 0;
			y_stop = screenFrag.getHeight() - smallFragment.getHeight() + 1;
			x_stop = screenFrag.getWidth() - smallFragment.getWidth();
		}

		mp = smallFragment.findAllIn(screenFrag, x_start, y_start, x_stop, y_stop);
		if (mp != null) {// change position to center of fragment
			MatrixPosition center = smallFragment.center();
			for(int i = 0; i < mp.length; i++)
				mp[i] = mp[i].add(center);
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