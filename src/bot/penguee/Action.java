package bot.penguee;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Action {
	private Robot bot = null;
	private Screen screen = null;
	private MatrixPosition lastCoord = null;
	private int mouseDelay = 10;
	private int keyboardDelay = 10;

	public Action() {
		try {
			screen = Data.screenObject;
			bot = new Robot();
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

	public void mouseClick(int x, int y, int button_mask) throws AWTException {
		mouseClick(x, y, button_mask, mouseDelay);
	}

	public void mouseClick(int x, int y, int button_mask, int sleepTime)
			throws AWTException {
		bot.mouseMove(x, y);
		bot.mousePress(button_mask);
		sleep(sleepTime);
		bot.mouseRelease(button_mask);
	}

	public void mouseClick(MatrixPosition mp) throws AWTException {
		mouseClick(mp.x, mp.y);
	}

	public void mouseClick(MatrixPosition mp, int button_mask)
			throws AWTException {
		mouseClick(mp.x, mp.y, button_mask);
	}

	public void mouseClick(MatrixPosition mp, int button_mask, int sleepTime)
			throws AWTException {
		mouseClick(mp.x, mp.y, button_mask, sleepTime);
	}

	public MatrixPosition mousePos() {
		return new MatrixPosition(MouseInfo.getPointerInfo().getLocation());
	}

	// //////////END OF MOUSE_CLICK///////////////////////////////////////

	// //////////////////MOUSE PRESS/RELEASE/////////////////
	public void mousePress(int button_mask) {
		bot.mousePress(button_mask);
	}

	public void mouseRelease(int button_mask) {
		bot.mouseRelease(button_mask);
	}

	// //////////////////END OF MOUSE PRESS/RELEASE/////////////////

	// //////////////////MOUSE MOVE /////////////////
	public void mouseMove(MatrixPosition mp) throws AWTException {
		bot.mouseMove(mp.x, mp.y);
	}

	public void mouseMove(int x, int y) throws AWTException {
		bot.mouseMove(x, y);
	}

	// //////////////////EMD OF MOUSE MOVE /////////////////

	// //////////////////////////MOUSE DRAG
	// METHODS/////////////////////////////////

	public void mouseDragDrop(int button_mask, MatrixPosition mp1,
			MatrixPosition mp2) throws AWTException {
		mouseDragDrop(button_mask, mp1.x, mp1.y, mp2.x, mp2.y);
	}

	public void mouseDragDrop(int button_mask, int x1, int y1, int x2, int y2)
			throws AWTException {
		bot.mouseMove(x1, y1);
		bot.mousePress(button_mask);
		sleep(mouseDelay);
		bot.mouseMove(x2, y2);
		bot.mouseRelease(button_mask);
	}
	

	// ////////////////////END OF MOUSE DRAG METHODS////////////////////////

	// /////////////////////FIND//////////////////////////////////////////
	public boolean findClick(String fragName) throws AWTException,
			FragmentNotLoadedException {
		if (find(fragName)) {
			mouseMove(lastCoord);
			return true;
		}
		return false;
	}

	public boolean findMove(String fragName) throws AWTException,
			FragmentNotLoadedException {
		if (find(fragName)) {
			mouseMove(lastCoord);
			return true;
		}
		return false;
	}

	public boolean find(String fragName) throws AWTException,
			FragmentNotLoadedException {
		return findPos(fragName, fragName) != null;
	}

	public MatrixPosition findPos(String fragName) throws AWTException,
			FragmentNotLoadedException {
		return findPos(fragName, fragName);
	}

	public MatrixPosition findPos(String fragName, String customPosName)
			throws AWTException, FragmentNotLoadedException {
		MatrixPosition mp = screen.find(fragName, customPosName);
		if (mp != null)
			lastCoord = mp;
		return mp;
	}

	public MatrixPosition[] findAllPos(String fragName) throws AWTException,
			FragmentNotLoadedException {
		return screen.find_all(fragName);
	}

	public MatrixPosition[] findAllPos(String fragName, String customPosName)
			throws AWTException, FragmentNotLoadedException {
		return screen.find_all(fragName, customPosName);
	}

	public MatrixPosition recentPos() {
		return lastCoord;
	}

	public void searchRect(int x1, int y1, int x2, int y2) {
		screen.setSearchRect(x1, y1, x2, y2);
	}

	public void searchRect(MatrixPosition mp1, MatrixPosition mp2) {
		screen.setSearchRect(mp1, mp2);
	}

	public void searchRect() {
		screen.setSearchRect();
	}

	// //////////////////////END OF FIND
	// METHODS//////////////////////////////////////

	// //////////////KEYBOARD METHODS///////////////////

	public void keyPress(int key_mask) {
		bot.keyPress(key_mask);
	}

	public void keyPress(int... keys) {
		for (int key : keys)
			bot.keyPress(key);
	}

	public void keyRelease(int key_mask) {
		bot.keyRelease(key_mask);
	}

	public void keyRelease(int... keys) {
		for (int key : keys)
			bot.keyRelease(key);
	}

	public void keyClick(int key_mask) {
		bot.keyPress(key_mask);
		sleep(keyboardDelay);
		bot.keyRelease(key_mask);
	}

	public void keyClick(int... keys) {
		keyPress(keys);
		sleep(keyboardDelay);
		keyRelease(keys);
	}

	public void print(String text) throws AWTException {
		copy(text);
		paste();
	}

	// ///////////END OF KEYBOARD METHODS//////////////////

	// ///////////SCREEN METHODS///////////////////////

	public void grab() throws Exception {
		screen.grab();
	}

	public void grab(int x, int y, int w, int h) throws Exception {
		screen.grab_rect(x, y, w, h);
	}

	public void grab(MatrixPosition mp, int w, int h) throws Exception {
		screen.grab_rect(mp.x, mp.y, w, h);
	}

	public void grab(MatrixPosition p1, MatrixPosition p2) throws Exception {
		screen.grab_rect(p1, p2);
	}

	public void grab(Rectangle rect) throws Exception {
		screen.grab_rect(rect);
	}

	// ///////////END OF SCREEN METHODS//////////////////

	// /////////////////////////////HELPER PUBLIC
	// METHODS////////////////////////////

	public BufferedImage screenImage() {
		return screen.getImage();
	}

	public BufferedImage fragImage(String name) {
		return Data.fragments.get(name).getImage();
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

	// /////////////////////////////HELPER INTERNAL
	// METHODS////////////////////////////

	private void copy(String text) {
		Clipboard clipboard = getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
	}

	private void paste() throws AWTException {
		bot.keyPress(KeyEvent.VK_CONTROL);
		bot.keyPress(KeyEvent.VK_V);
		sleep(keyboardDelay);
		bot.keyRelease(KeyEvent.VK_CONTROL);
		bot.keyRelease(KeyEvent.VK_V);
	}

	/*
	 * private String get() throws Exception { Clipboard systemClipboard =
	 * getSystemClipboard(); DataFlavor dataFlavor = DataFlavor.stringFlavor;
	 * 
	 * if (systemClipboard.isDataFlavorAvailable(dataFlavor)) { Object text =
	 * systemClipboard.getData(dataFlavor); return (String) text; }
	 * 
	 * return null; }
	 */
	private Clipboard getSystemClipboard() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		Clipboard systemClipboard = defaultToolkit.getSystemClipboard();
		return systemClipboard;
	}

}
