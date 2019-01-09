package bot.penguee;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import bot.penguee.exception.FragmentNotLoadedException;
import bot.penguee.exception.ScreenNotGrabbedException;

public class Action {
	private Robot robot = null;
	private Screen screen = null;
	private MatrixPosition lastCoord = null;
	private int mouseDelay = 10;
	private int keyboardDelay = 10;

	public Action() {
		try {
			screen = Data.getForceUseGPU() ? new ScreenGPU() : new Screen();
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

	// simplify thread sleep method, to be used in a script
	public void sleep(int ms) {
		if (ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {

			}
		}
	}

	// //////////MOUSE_CLICK////////////////////////////////////////////////////////////////////////////////////////
	public void mouseClick(int x, int y) throws AWTException {
		mouseClick(x, y, InputEvent.BUTTON1_MASK, mouseDelay);
	}

	public void mouseRightClick(int x, int y) throws AWTException {
		mouseClick(x, y, InputEvent.BUTTON3_MASK, mouseDelay);
	}

	public void mouseClick(int x, int y, int button_mask) throws AWTException {
		mouseClick(x, y, button_mask, mouseDelay);
	}

	public void mouseClick(int x, int y, int button_mask, int sleepTime) throws AWTException {
		mouseMove(x, y);
		mousePress(button_mask);
		sleep(sleepTime);
		mouseRelease(button_mask);
	}

	public void mouseClick(MatrixPosition mp) throws AWTException {
		mouseClick(mp.x, mp.y);
	}

	public void mouseClick(MatrixPosition mp, int button_mask) throws AWTException {
		mouseClick(mp.x, mp.y, button_mask);
	}

	public void mouseClick(MatrixPosition mp, int button_mask, int sleepTime) throws AWTException {
		mouseClick(mp.x, mp.y, button_mask, sleepTime);
	}

	public void mouseWheel(int wheel) {
		robot.mouseWheel(wheel);
	}

	public MatrixPosition mousePos() {
		return new MatrixPosition(MouseInfo.getPointerInfo().getLocation());
	}

	// //////////END OF MOUSE_CLICK///////////////////////////////////////

	// //////////////////MOUSE PRESS/RELEASE/////////////////
	public void mousePress(int button_mask) {
		robot.mousePress(button_mask);
	}

	public void mouseRelease(int button_mask) {
		robot.mouseRelease(button_mask);
	}

	public void mousePress() {
		mousePress(InputEvent.BUTTON1_MASK);
	}

	public void mouseRelease() {
		mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public void mouseRightPress() {
		mousePress(InputEvent.BUTTON3_MASK);
	}

	public void mouseRightRelease() {
		mouseRelease(InputEvent.BUTTON3_MASK);
	}

	// //////////////////END OF MOUSE PRESS/RELEASE/////////////////

	// //////////////////MOUSE MOVE /////////////////
	public void mouseMove(MatrixPosition mp) throws AWTException {
		mouseMove(mp.x, mp.y);
	}

	public void mouseMove(int x, int y) throws AWTException {
		robot.mouseMove(x, y);
	}

	// //////////////////EMD OF MOUSE MOVE /////////////////

	// //////////////////////////MOUSE DRAG
	// METHODS/////////////////////////////////

	public void mouseDragDrop(int button_mask, MatrixPosition mp1, MatrixPosition mp2) throws AWTException {
		mouseDragDrop(button_mask, mp1.x, mp1.y, mp2.x, mp2.y);
	}

	public void mouseDragDrop(int button_mask, int x1, int y1, int x2, int y2) throws AWTException {
		mouseMove(x1, y1);
		mousePress(button_mask);
		sleep(mouseDelay);
		mouseMove(x2, y2);
		mouseRelease(button_mask);
	}

	// ////////////////////END OF MOUSE DRAG METHODS////////////////////////

	// /////////////////////FIND//////////////////////////////////////////
	public boolean findClick(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		if (find(fragName)) {
			mouseClick(lastCoord);
			return true;
		}
		return false;
	}

	public boolean findMove(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		if (find(fragName)) {
			mouseMove(lastCoord);
			return true;
		}
		return false;
	}

	public boolean find(String fragName) throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return findPos(fragName, fragName) != null;
	}

	public MatrixPosition findPos(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return findPos(fragName, fragName);
	}

	public MatrixPosition findPos(String fragName, String customPosName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		MatrixPosition mp = screen.find(fragName, customPosName);
		if (mp != null)
			lastCoord = mp;
		return mp;
	}

	public MatrixPosition[] findAllPos(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return screen.find_all(fragName);
	}

	public MatrixPosition[] findAllPos(String fragName, String customPosName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return screen.find_all(fragName, customPosName);
	}

	private boolean waitFor(String fragName, int time, int delay, MatrixPosition mp1, MatrixPosition mp2,
			boolean expect) throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		long timeStop = System.currentTimeMillis() + time;
		MatrixPosition[] searchRectBackup = screen.getSearchRect();
		boolean searchInRegionBackup = screen.getSearchInRegion();
		searchRect(mp1, mp2);
		boolean result = false;

		while (timeStop > System.currentTimeMillis()) {
			grab(mp1, mp2);
			if (find(fragName) == expect) {
				result = true;
				break;
			}
			sleep(delay);
		}
		if (searchInRegionBackup)
			searchRect(searchRectBackup[0], searchRectBackup[1]);
		else
			searchRect();
		return result;
	}

	public boolean waitFor(String fragName, int time, int delay, MatrixPosition mp1, MatrixPosition mp2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, mp1, mp2, true);
	}

	public boolean waitFor(String fragName, int time, int delay, MatrixPosition mp1, int w, int h)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, mp1, mp1.add(w, h));
	}

	public boolean waitFor(String fragName, int time, int delay, int x1, int y1, int x2, int y2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, new MatrixPosition(x1, y1), new MatrixPosition(x2, y2));
	}

	public boolean waitFor(String fragName, int time) throws Exception {
		Rectangle rect = screen.getRect();
		return waitFor(fragName, time, 0, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	public boolean waitFor(String fragName, int time, int delay) throws Exception {
		Rectangle rect = screen.getRect();
		return waitFor(fragName, time, delay, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	public boolean waitForHide(String fragName, int time, int delay, MatrixPosition mp1, MatrixPosition mp2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, mp1, mp2, false);
	}

	public boolean waitForHide(String fragName, int time, int delay, MatrixPosition mp1, int w, int h)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitForHide(fragName, time, delay, mp1, mp1.add(w, h));
	}

	public boolean waitForHide(String fragName, int time, int delay, int x1, int y1, int x2, int y2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitForHide(fragName, time, delay, new MatrixPosition(x1, y1), new MatrixPosition(x2, y2));
	}

	public boolean waitForHide(String fragName, int time) throws Exception {
		Rectangle rect = screen.getRect();
		return waitForHide(fragName, time, 0, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	public boolean waitForHide(String fragName, int time, int delay) throws Exception {
		Rectangle rect = screen.getRect();
		return waitForHide(fragName, time, delay, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	public boolean findSimilar(String fragName, double rate)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return findSimilarPos(fragName, rate, fragName) != null;
	}

	public MatrixPosition findSimilarPos(String fragName, double rate)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return findSimilarPos(fragName, rate, fragName);
	}

	public MatrixPosition findSimilarPos(String fragName, double rate, String customName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		MatrixPosition mp = screen.findSimilar(fragName, rate, customName);
		if (mp != null)
			lastCoord = mp;
		return mp;
	}

	public MatrixPosition recentPos() {
		return lastCoord;
	}

	public void searchRect(int x1, int y1, int x2, int y2) {
		screen.setSearchRect(x1, y1, x2, y2);
	}

	public void searchRect(MatrixPosition mp1, int w, int h) {
		screen.setSearchRect(mp1.x, mp1.y, mp1.x + w, mp1.y + h);
	}

	public void searchRect(MatrixPosition mp1, MatrixPosition mp2) {
		screen.setSearchRect(mp1, mp2);
	}

	public void searchRect() {
		screen.setSearchRect();
	}

	public MatrixPosition[] getSearchRect() {
		return screen.getSearchRect();
	}

	// //////////////////////END OF FIND
	// METHODS//////////////////////////////////////

	// //////////////KEYBOARD METHODS///////////////////

	public void keyPress(int key_mask) {
		robot.keyPress(key_mask);
	}

	public void keyRelease(int key_mask) {
		robot.keyRelease(key_mask);
	}

	public void keyPress(int... keys) {
		for (int key : keys)
			keyPress(key);
	}

	public void keyRelease(int... keys) {
		for (int key : keys)
			keyRelease(key);
	}

	public void keyClick(int key_mask) {
		keyPress(key_mask);
		sleep(keyboardDelay);
		keyRelease(key_mask);
	}

	public void keyClick(int... keys) {
		keyPress(keys);
		sleep(keyboardDelay);
		keyRelease(keys);
	}

	public void print(String text) throws AWTException {
		write(text);
		paste();
	}

	public void writeClipboard(String text) {
		write(text);
	}

	public String readClipboard() {
		return read();
	}

	// ///////////END OF KEYBOARD METHODS//////////////////

	// ///////////SCREEN METHODS///////////////////////

	public void grab() throws Exception {
		screen.grab();
	}

	public void grab(int x1, int y1, int x2, int y2) throws Exception {
		grab(new Rectangle(x1, y1, x2 - x1, y2 - y1));
	}

	public void grab(MatrixPosition mp, int w, int h) throws Exception {
		grab(new Rectangle(mp.x, mp.y, w, h));
	}

	public void grab(MatrixPosition p1, MatrixPosition p2) throws Exception {
		grab(new Rectangle(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y));
	}

	public void grab(Rectangle rect) throws Exception {
		screen.grab(rect);
	}

	// ///////////END OF SCREEN METHODS//////////////////

	// /////////////////////////////HELPER PUBLIC
	// METHODS////////////////////////////

	public BufferedImage screenImage() {
		return screen.getImage();
	}

	public int screenPixel(int x, int y) {
		return screen.getPixel(x, y);
	}

	public byte[] screenPixelARGB(int x, int y) {
		int p = screenPixel(x, y);
		return new byte[] { (byte) (p >>> 24), (byte) (p >>> 16), (byte) (p >>> 8), (byte) p };
	}

	public BufferedImage fragImage(String name) {
		return Data.fragments().get(name).getImage();
	}

	public int getMouseDelay() {
		return mouseDelay;
	}

	public void setMouseDelay(int mouseDelay) {
		this.mouseDelay = mouseDelay;
	}

	public int getKeyboardDelay() {
		return keyboardDelay;
	}

	public void setKeyboardDelay(int keyboardDelay) {
		this.keyboardDelay = keyboardDelay;
	}

	public String getVersion() {
		return Update.version;
	}

	public void reloadFrags() {
		Data.loadFragments(false); // console log turned off by false argument
	}

	public void createFrag(String name, MatrixPosition p1, MatrixPosition p2) throws Exception {
		createFrag(name, p1.x, p1.y, p2.x, p2.y);
	}

	public void createFrag(String name, int x1, int y1, int x2, int y2) throws Exception {
		createFrag(name, x1, y1, x2 - x1, y2 - y1);
	}

	public void createFrag(String name, MatrixPosition p1, int w, int h) throws Exception {
		new Frag(screenImage().getSubimage(p1.x, p1.y, w, h)).makeFile(name);
	}

	public Object[] listFragNames() {
		return Data.getFragmentKeys();
	}

	// /////////////////////////////HELPER INTERNAL
	// METHODS////////////////////////////

	private void write(String text) {
		Clipboard clipboard = getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
	}

	private String read() {
		try {
			return (String) getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void paste() throws AWTException {
		keyPress(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
		sleep(keyboardDelay);
		keyRelease(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
	}

	private Clipboard getSystemClipboard() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		Clipboard systemClipboard = defaultToolkit.getSystemClipboard();
		return systemClipboard;
	}

	public Method[] getAPI() {
		List<Method> result = new ArrayList<Method>();
		for (Method method : this.getClass().getDeclaredMethods()) {
			int modifiers = method.getModifiers();
			if (Modifier.isPublic(modifiers)) {
				result.add(method);
			}
		}

		return result.toArray(new Method[result.size()]);
	}

}
