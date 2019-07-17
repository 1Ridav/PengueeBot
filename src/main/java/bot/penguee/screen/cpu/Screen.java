package bot.penguee.screen.cpu;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import bot.penguee.Data;
import bot.penguee.Position;
import bot.penguee.Region;
import bot.penguee.exception.FragmentNotLoadedException;
import bot.penguee.exception.ScreenNotGrabbedException;
import bot.penguee.fragments.Frag;
import bot.penguee.screen.ScreenEngineInterface;

public class Screen implements ScreenEngineInterface{
	private final Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	private BufferedImage image = null;
	private Frag screenFrag = null;
	private Robot robot;
	private HashMap<Frag, Position> cache;

	private boolean searchInRegion = false;
	private Region searchRect = null;

	private boolean useCache = Data.getUseInternalCache();

	public Screen() {
		cache = new HashMap<Frag, Position>();
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

	public int getPixel(int x, int y) {
		return screenFrag.getRgbData()[y][x];
	}

	public Rectangle getRect() {
		return screenRect;
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

	public void grab(Rectangle rect) throws Exception {

		if (screenFrag == null) {
			grab();
		} else {
			image = robot.createScreenCapture(rect);
			// REFRESH MATRIX BUFFER
			screenFrag.getIntRGB(image, rect.x, rect.y);
		}
	}

	// SET RECTANGLE WHERE FRAGMENS BEING SEARCHED
	public void setSearchRect(int x1, int y1, int x2, int y2) {
		setSearchRect(new Position(x1, y1), new Position(x2, y2));
	}

	public void setSearchRect(Position mp1, Position mp2) {
		setSearchRect(new Region(mp1, mp2));
	}

	public void setSearchRect(Region mr) {
		searchRect = mr;
		searchInRegion = true;
	}

	// SETS SEARCH RECTANGLE TO WHOLE SCREEN
	public void setSearchRect() {
		searchInRegion = false;
	}

	public Region getSearchRect() {
		if (searchInRegion)
			return searchRect;
		else
			return null;
	}

	public boolean getSearchInRegion() {
		return searchInRegion;
	}

	public Position find(Frag smallFragment) throws ScreenNotGrabbedException {
		Position cachedPos = null;

		if (screenFrag == null)
			throw new ScreenNotGrabbedException();
		if (useCache)
			cachedPos = cache.get(smallFragment);
		Position mp = null;
		if (cachedPos != null) { // fragment was cached previously, ensure it is
									// still there
			if (searchInRegion) {
				if (searchRect.bounds(cachedPos)) {
					mp = smallFragment.findIn(screenFrag, cachedPos.x, cachedPos.y, cachedPos.x + 1, cachedPos.y + 1);
				}
			} else {
				mp = smallFragment.findIn(screenFrag, cachedPos.x, cachedPos.y, cachedPos.x + 1, cachedPos.y + 1);
			}
		}

		if (mp == null) {// cache miss
			int x_start, y_start, x_stop, y_stop;
			if (searchInRegion) {
				y_start = searchRect.p1.y;
				x_start = searchRect.p1.x;
				y_stop = searchRect.p2.y - smallFragment.getHeight() + 1;
				x_stop = searchRect.p2.x - smallFragment.getWidth();

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
			return mp.add(smallFragment.center());// change position to center of fragment and return
		}
		return null;
	}

	public Position findSimilar(Frag smallFragment, double rate) throws ScreenNotGrabbedException {
		Position cachedPos = null;

		if (screenFrag == null)
			throw new ScreenNotGrabbedException();
		if (useCache)
			cachedPos = cache.get(smallFragment);
		Position mp = null;
		if (cachedPos != null) { // fragment was cached previously, ensure it is
									// still there
			if (searchInRegion) {
				if (searchRect.bounds(cachedPos)) {
					mp = smallFragment.findIn(screenFrag, cachedPos.x, cachedPos.y, cachedPos.x + 1, cachedPos.y + 1);
				}
			} else {
				mp = smallFragment.findSimilarIn(screenFrag, rate, cachedPos.x, cachedPos.y, cachedPos.x + 1,
						cachedPos.y + 1);
			}
		}

		if (mp == null) {// cache miss
			int x_start, y_start, x_stop, y_stop;
			if (searchInRegion) {
				y_start = searchRect.p1.y;
				x_start = searchRect.p1.x;
				y_stop = searchRect.p2.y - smallFragment.getHeight() + 1;
				x_stop = searchRect.p2.x - smallFragment.getWidth();
			} else {
				y_start = 0;
				x_start = 0;
				y_stop = screenFrag.getHeight() - smallFragment.getHeight() + 1;
				x_stop = screenFrag.getWidth() - smallFragment.getWidth();
			}
			mp = smallFragment.findSimilarIn(screenFrag, rate, x_start, y_start, x_stop, y_stop);
		}

		if (mp != null) {
			if (useCache)
				cache.put(smallFragment, mp);
			return mp.add(smallFragment.center());// change position to center of fragment and return
		}
		return null;
	}

	public Position[] find_all(Frag smallFragment) throws ScreenNotGrabbedException {

		Position[] mp = null;
		int x_start, y_start, x_stop, y_stop;

		if (screenFrag == null)
			throw new ScreenNotGrabbedException();

		if (searchInRegion) {
			y_start = searchRect.p1.y;
			x_start = searchRect.p1.x;
			y_stop = searchRect.p2.y - smallFragment.getHeight() + 1;
			x_stop = searchRect.p2.x - smallFragment.getWidth();

		} else {
			y_start = 0;
			x_start = 0;
			y_stop = screenFrag.getHeight() - smallFragment.getHeight() + 1;
			x_stop = screenFrag.getWidth() - smallFragment.getWidth();
		}

		mp = smallFragment.findAllIn(screenFrag, x_start, y_start, x_stop, y_stop);
		if (mp != null) {// change position to center of fragment
			Position center = smallFragment.center();
			for (int i = 0; i < mp.length; i++)
				mp[i] = mp[i].add(center);
		}

		return mp;
	}

	Position findSimilar(String name, double rate) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return findSimilar(name, rate, name);
	}

	public Position findSimilar(String name, double rate, String customName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		Frag f = Data.fragments().get(name);
		if (f != null) {
			Position mp = findSimilar(f, rate);
			if (mp != null)
				return mp.setName(customName);
			return null;
		} else {
			throw new FragmentNotLoadedException(name);
		}
	}

	Position find(String name) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return find(name, name);
	}

	public Position find(String name, String customName) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		Frag f = Data.fragments().get(name);
		if (f != null) {
			Position mp = find(f);
			if (mp != null)
				return mp.setName(customName);
			return null;
		} else {
			throw new FragmentNotLoadedException(name);
		}
	}

	public Position[] find_all(String name) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return find_all(name, name);
	}

	public Position[] find_all(String name, String customName) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		Frag f = Data.fragments().get(name);
		if (f != null) {
			Position mp[] = find_all(f);
			if (mp != null)
				for (Position p : mp)
					p.setName(customName);
			return mp;
		} else {
			throw new FragmentNotLoadedException(name);
		}
	}
}
