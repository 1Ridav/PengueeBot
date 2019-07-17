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
import java.awt.image.BufferedImage;
import java.io.IOException;

import bot.penguee.exception.FragmentNotLoadedException;
import bot.penguee.exception.ScreenNotGrabbedException;
import bot.penguee.fragments.Frag;
import bot.penguee.screen.ScreenEngine;
import bot.penguee.screen.ScreenEngineInterface;

public class Action {
	private Robot robot = null;
	private ScreenEngineInterface screen;
	private Position lastCoord = null;
	private int mouseDelay = 10;
	private int keyboardDelay = 10;

	public Action() throws AWTException {

		screen = new ScreenEngine();
		robot = new Robot();
	}

	// simplify thread sleep method, to be used in a script
	/**
	 * 
	 * @param ms
	 *            Wait for milliseconds, 1 second = 1000 milliseconds
	 */
	public void sleep(int ms) {
		if (ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {

			}
		}
	}

	// //////////MOUSE_CLICK////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Move cursor to selected position on the screen and perform left mouse button
	 * click
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseClick(int x, int y) throws AWTException {
		mouseClick(x, y, InputEvent.BUTTON1_MASK, mouseDelay);
	}

	/**
	 * Move cursor to selected position on the screen and perform right mouse button
	 * click
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseRightClick(int x, int y) throws AWTException {
		mouseClick(x, y, InputEvent.BUTTON3_MASK, mouseDelay);
	}

	/**
	 * Move cursor to selected position on the screen and perform click on mouse
	 * button you specify in mask
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseClick(int x, int y, int button_mask) throws AWTException {
		mouseClick(x, y, button_mask, mouseDelay);
	}

	/**
	 * Move cursor to selected position on the screen and perform click on the mouse
	 * button you specify in mask, you can also adjust press-release delay
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 * @param sleepTime
	 *            Delay between press and release actions in milliseconds
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseClick(int x, int y, int button_mask, int sleepTime) throws AWTException {
		mouseMove(x, y);
		mousePress(button_mask);
		sleep(sleepTime);
		mouseRelease(button_mask);
	}

	/**
	 * Move cursor to selected position on the screen and perform left mouse button
	 * click
	 * 
	 * @param mp
	 *            Position that store X and Y axis coordinates
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseClick(Position mp) throws AWTException {
		mouseClick(mp.x, mp.y);
	}

	/**
	 * Move cursor to selected position on the screen and perform click on the mouse
	 * button you specify in mask
	 * 
	 * @param mp
	 *            Position that store X and Y axis coordinates
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseClick(Position mp, int button_mask) throws AWTException {
		mouseClick(mp.x, mp.y, button_mask);
	}

	/**
	 * Move cursor to selected position on the screen and perform click on the mouse
	 * button you specify in mask, you can also adjust press-release delay
	 * 
	 * @param mp
	 *            Position that store X and Y axis coordinates
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 * @param sleepTime
	 *            Delay between press and release actions in milliseconds
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseClick(Position mp, int button_mask, int sleepTime) throws AWTException {
		mouseClick(mp.x, mp.y, button_mask, sleepTime);
	}

	/**
	 * Perform mouse scroll
	 * 
	 * @param wheel
	 *            Any positive or negative value 100 / -100 to scroll
	 */
	public void mouseWheel(int wheel) {
		robot.mouseWheel(wheel);
	}

	/**
	 * Get current mouse cursor position on the screen
	 * 
	 * @return Position that contains x and y values
	 */
	public Position mousePos() {
		return new Position(MouseInfo.getPointerInfo().getLocation());
	}

	// //////////END OF MOUSE_CLICK///////////////////////////////////////

	// //////////////////MOUSE PRESS/RELEASE/////////////////
	/**
	 * Press selected mouse button
	 * 
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 */
	public void mousePress(int button_mask) {
		robot.mousePress(button_mask);
	}

	/**
	 * Release selected mouse button (previously pressed)
	 * 
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 */
	public void mouseRelease(int button_mask) {
		robot.mouseRelease(button_mask);
	}

	/**
	 * A simplified version of left mouse button press action
	 */
	public void mousePress() {
		mousePress(InputEvent.BUTTON1_MASK);
	}

	/**
	 * A simplified version of left mouse button release action
	 */
	public void mouseRelease() {
		mouseRelease(InputEvent.BUTTON1_MASK);
	}

	/**
	 * A simplified version of right mouse button press action
	 */
	public void mouseRightPress() {
		mousePress(InputEvent.BUTTON3_MASK);
	}

	/**
	 * A simplified version of right mouse button release action
	 */
	public void mouseRightRelease() {
		mouseRelease(InputEvent.BUTTON3_MASK);
	}

	// //////////////////END OF MOUSE PRESS/RELEASE/////////////////

	// //////////////////MOUSE MOVE /////////////////
	/**
	 * Moves mouse cursor to a position on the screen
	 * 
	 * @param mp
	 *            Position that contains position on the screen
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseMove(Position mp) throws AWTException {
		mouseMove(mp.x, mp.y);
	}

	/**
	 * Moves mouse cursor to a position on the screen
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseMove(int x, int y) throws AWTException {
		robot.mouseMove(x, y);
	}

	// //////////////////EMD OF MOUSE MOVE /////////////////

	// //////////////////////////MOUSE DRAG
	// METHODS/////////////////////////////////
	/**
	 * Drag and Drop operation, select button that will be used for dragging, FROM
	 * and TO positions on the screen
	 * 
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 * @param mp1
	 *            Position from where
	 * @param mp2
	 *            Position to where
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseDragDrop(int button_mask, Position mp1, Position mp2) throws AWTException {
		mouseDragDrop(button_mask, mp1.x, mp1.y, mp2.x, mp2.y);
	}

	/**
	 * Drag and Drop operation, select button that will be used for dragging, FROM
	 * and TO positions on the screen
	 * 
	 * @param button_mask
	 *            Button mask, InputEvent.BUTTON1_MASK for example
	 * @param x1
	 *            X axis value FROM
	 * @param y1
	 *            Y axis value FROM
	 * @param x2
	 *            X axis value TO
	 * @param y2
	 *            Y axis value TO
	 * @throws AWTException
	 *             AWTException
	 */
	public void mouseDragDrop(int button_mask, int x1, int y1, int x2, int y2) throws AWTException {
		mouseMove(x1, y1);
		sleep(1);
		mousePress(button_mask);
		sleep(mouseDelay);
		mouseMove(x2, y2);
		sleep(1);
		mouseRelease(button_mask);
	}

	// ////////////////////END OF MOUSE DRAG METHODS////////////////////////

	// /////////////////////FIND//////////////////////////////////////////
	/**
	 * Find exact match of the selected fragment on the screen, move mouse cursor to
	 * the center of that fragment position and click on left mouse button. Tip:
	 * This method store last Position, that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @return true or false
	 * @throws AWTException
	 *             AWTException
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */
	public boolean findClick(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		if (find(fragName)) {
			mouseClick(lastCoord);
			return true;
		}
		return false;
	}

	/**
	 * Find exact match of the selected fragment on the screen and move mouse cursor
	 * to the center of that fragment position Tip: This method store last Position,
	 * that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @return true or false
	 * @throws AWTException
	 *             AWTException
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */

	public boolean findMove(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		if (find(fragName)) {
			mouseMove(lastCoord);
			return true;
		}
		return false;
	}

	/**
	 * Find exact match of the selected fragment on the screen. Tip: This method
	 * store last Position, that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @return true or false
	 * @throws AWTException
	 *             AWTException
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */
	public boolean find(String fragName) throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return findPos(fragName, fragName) != null;
	}

	/**
	 * Find exact match of the selected fragment on the screen and return position
	 * on the screen Tip: This method store last Position, that can be accessed via
	 * recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @return null or Position
	 * @throws AWTException
	 *             AWTException
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */
	public Position findPos(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return findPos(fragName, fragName);
	}

	/**
	 * Tip: This method store last Position, that can be accessed via recentPos()
	 * method. Find exact match of the selected fragment on the screen and return
	 * position on the screen
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param customPosName
	 *            Custom name that will be assigned to Position
	 * @return null or Position
	 * @throws AWTException
	 *             AWTException
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */
	public Position findPos(String fragName, String customPosName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		Position mp = screen.find(fragName, customPosName);
		if (mp != null)
			lastCoord = mp;
		return mp;
	}

	/**
	 * Find all exact matches of the selected fragment on the screen and return
	 * positions on the screen
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @return null or Position[]
	 * @throws AWTException
	 *             AWTException
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */

	public Position[] findAllPos(String fragName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return screen.find_all(fragName);
	}

	/**
	 * Find all exact matches of the selected fragment on the screen and return
	 * positions on the screen
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param customPosName
	 *            Custom name that will be assigned to Position
	 * @return null or Position[]
	 * @throws AWTException
	 *             AWTException
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */

	public Position[] findAllPos(String fragName, String customPosName)
			throws AWTException, FragmentNotLoadedException, ScreenNotGrabbedException {
		return screen.find_all(fragName, customPosName);
	}

	private boolean waitFor(String fragName, int time, int delay, Position mp1, Position mp2, boolean expect)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		long timeStop = System.currentTimeMillis() + time;
		Region searchRectBackup = screen.getSearchRect();
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
			searchRect(searchRectBackup);
		else
			searchRect();
		return result;
	}

	/**
	 * Wait for particular fragment appear on the screen, continuously capture the
	 * screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has appeared, false if fragment was not found until timeout
	 * Tip: This method store last Position, that can be accessed via recentPos()
	 * method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @param mp1
	 *            Position Searching limit bounds (upper left corner)
	 * @param mp2
	 *            Position Searching limit bounds (bottom right corner)
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */

	public boolean waitFor(String fragName, int time, int delay, Position mp1, Position mp2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, mp1, mp2, true);
	}

	/**
	 * Wait for particular fragment appear on the screen, continuously capture the
	 * screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has appeared, false if fragment was not found until
	 * timeout. Tip: This method store last Position, that can be accessed via
	 * recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @param mp1
	 *            Position Searching limit bounds (upper left corner)
	 * @param w
	 *            Width
	 * @param h
	 *            Height
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */
	public boolean waitFor(String fragName, int time, int delay, Position mp1, int w, int h)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, mp1, mp1.add(w, h));
	}

	/**
	 * Wait for particular fragment appear on the screen, continuously capture the
	 * screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has appeared, false if fragment was not found until timeout
	 * Tip: This method store last Position, that can be accessed via recentPos()
	 * method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @param x1
	 *            Searching limit bounds (upper left corner)
	 * @param y1
	 *            Searching limit bounds (upper left corner)
	 * @param x2
	 *            Searching limit bounds (bottom right corner)
	 * @param y2
	 *            Searching limit bounds (bottom right corner)
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */
	public boolean waitFor(String fragName, int time, int delay, int x1, int y1, int x2, int y2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, new Position(x1, y1), new Position(x2, y2));
	}

	/**
	 * Wait for particular fragment appear on the screen, continuously capture the
	 * screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has appeared, false if fragment was not found until timeout
	 * This method search on whole screen regarding to searchRect() Tip: This method
	 * store last Position, that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */
	public boolean waitFor(String fragName, int time)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		Rectangle rect = screen.getRect();
		return waitFor(fragName, time, 0, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Wait for particular fragment appear on the screen, continuously capture the
	 * screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has appeared, false if fragment was not found until timeout
	 * This method search on whole screen regarding to searchRect() Tip: This method
	 * store last Position, that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */
	public boolean waitFor(String fragName, int time, int delay) throws Exception {
		Rectangle rect = screen.getRect();
		return waitFor(fragName, time, delay, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Wait for particular fragment disappear from the screen, continuously capture
	 * the screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has disappeared, false if fragment still being found Tip:
	 * Limiting searching bounds will affect performance and reduce searching delays
	 * Tip: This method store last Position, that can be accessed via recentPos()
	 * method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @param mp1
	 *            Position Searching limit bounds (upper left corner)
	 * @param mp2
	 *            Position Searching limit bounds (bottom right corner)
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */
	public boolean waitForHide(String fragName, int time, int delay, Position mp1, Position mp2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitFor(fragName, time, delay, mp1, mp2, false);
	}

	/**
	 * Wait for particular fragment disappear from the screen, continuously capture
	 * the screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has disappeared, false if fragment still being found Tip:
	 * Limiting searching bounds will affect performance and reduce searching delays
	 * Tip: This method store last Position, that can be accessed via recentPos()
	 * method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @param mp1
	 *            Position Searching limit bounds (upper left corner)
	 * @param w
	 *            Width
	 * @param h
	 *            Height
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */
	public boolean waitForHide(String fragName, int time, int delay, Position mp1, int w, int h)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitForHide(fragName, time, delay, mp1, mp1.add(w, h));
	}

	/**
	 * Wait for particular fragment disappear from the screen, continuously capture
	 * the screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has disappeared, false if fragment still being found Tip:
	 * Limiting searching bounds will affect performance and reduce searching delays
	 * Tip: This method store last Position, that can be accessed via recentPos()
	 * method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @param x1
	 *            Searching limit bounds (upper left corner)
	 * @param y1
	 *            Searching limit bounds (upper left corner)
	 * @param x2
	 *            Searching limit bounds (bottom right corner)
	 * @param y2
	 *            Searching limit bounds (bottom right corner)
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */
	public boolean waitForHide(String fragName, int time, int delay, int x1, int y1, int x2, int y2)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		return waitForHide(fragName, time, delay, new Position(x1, y1), new Position(x2, y2));
	}

	/**
	 * Wait for particular fragment disappear from the screen, continuously capture
	 * the screen and search for exact matching fragment on the screen. Will return
	 * true, if fragment has disappeared, false if fragment still being found Tip:
	 * This method store last Position, that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */

	public boolean waitForHide(String fragName, int time)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		Rectangle rect = screen.getRect();
		return waitForHide(fragName, time, 0, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Wait for particular fragment disappear from the screen, continuously capture
	 * the screen without any delay between captures and search for exact matching
	 * fragment on the screen. Will return true, if fragment has disappeared, false
	 * if fragment still being found Tip: This method store last Position, that can
	 * be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param time
	 *            Maximum delay until return false by timeout
	 * @param delay
	 *            Delay between screen captures, this will affect overall system
	 *            load
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 * @throws Exception
	 *             Exception
	 */

	public boolean waitForHide(String fragName, int time, int delay)
			throws FragmentNotLoadedException, ScreenNotGrabbedException, Exception {
		Rectangle rect = screen.getRect();
		return waitForHide(fragName, time, delay, 0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Works similar to find() method, except it will not try to find exact match on
	 * the screen, but it will try to find best match which rate is higher than you
	 * specified. This method will also find exact match just like findPos() Tip:
	 * Use this if some pixels differ, this method is able to find fragment under
	 * noise conditions Tip 2: This method is very slow, so try to avoid searching
	 * on whole screen, searchRect() method will help you to limit searching bounds
	 * and reduce searching delay Tip 3: This method store last Position, that can
	 * be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param rate
	 *            similarity rate 0.9 is 90%, 0.99 is 99%
	 * @return true or false
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */
	public boolean findSimilar(String fragName, double rate)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return findSimilarPos(fragName, rate, fragName) != null;
	}

	/**
	 * Works similar to findPos() method, except it will not try to find exact match
	 * on the screen, but it will try to find best match which rate is higher than
	 * you specified. This method will also find exact match just like findPos()
	 * Tip: Use this if some pixels differ, this method is able to find fragment
	 * under noise conditions Tip 2: This method is very slow, so try to avoid
	 * searching on whole screen, searchRect() method will help you to limit
	 * searching bounds and reduce searching delay Tip 3: This method store last
	 * Position, that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param rate
	 *            similarity rate 0.9 is 90%, 0.99 is 99%
	 * @return NULL or Position position on the screen
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */
	public Position findSimilarPos(String fragName, double rate)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return findSimilarPos(fragName, rate, fragName);
	}

	/**
	 * Works similar to findPos() method, except it will not try to find exact match
	 * on the screen, but it will try to find best match which rate is higher than
	 * you specified. This method will also find exact match just like findPos()
	 * Tip: Use this if some pixels differ, this method is able to find fragment
	 * under noise conditions Tip 2: This method is very slow, so try to avoid
	 * searching on whole screen, searchRect() method will help you to limit
	 * searching bounds and reduce searching delay Tip 3: This method store last
	 * Position, that can be accessed via recentPos() method
	 * 
	 * @param fragName
	 *            Regular fragment name you are trying to find on the screen
	 * @param rate
	 *            similarity rate 0.9 is 90%, 0.99 is 99%
	 * @param customName
	 *            Position name, if fragment has been found on the screen
	 * @return NULL or Position position on the screen
	 * @throws FragmentNotLoadedException
	 *             Occurs if fragment was not loaded, check if fragment name is
	 *             correct
	 * @throws ScreenNotGrabbedException
	 *             Occurs if grab() was not called before calling this method
	 */
	public Position findSimilarPos(String fragName, double rate, String customName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		Position mp = screen.findSimilar(fragName, rate, customName);

		if (mp != null)
			lastCoord = mp;
		return mp;
	}

	/**
	 * Get last successful fragment search(Fragment has been found) position on the
	 * screen After each single fragment search operation, last position is being
	 * saved and could be accessed via this method
	 * 
	 * Tip: Very useful to make code look pretty
	 * 
	 * @return Position of last fragment being found in single (find() findClick()
	 *         and etc...)
	 */
	public Position recentPos() {
		return lastCoord;
	}

	/**
	 * Set search bounds limit, so next search will be performed on limited region,
	 * this will affect on fragment searching time(reduce searching delay) Tip: This
	 * can improve searching speed, if you surely know, where your fragment will
	 * appear
	 * 
	 * @param x1
	 *            Searching limit bounds (upper left corner)
	 * @param y1
	 *            Searching limit bounds (upper left corner)
	 * @param x2
	 *            Searching limit bounds (bottom right corner)
	 * @param y2
	 *            Searching limit bounds (bottom right corner)
	 */
	public void searchRect(int x1, int y1, int x2, int y2) {
		screen.setSearchRect(x1, y1, x2, y2);
	}

	/**
	 * Set search bounds limit, so next search will be performed on limited region,
	 * this will affect on fragment searching time(reduce searching delay) Tip: This
	 * can improve searching speed, if you surely know, where your fragment will
	 * appear
	 * 
	 * @param mp1
	 *            Searching limit bounds (upper left corner)
	 * @param w
	 *            Width
	 * @param h
	 *            Height
	 */
	public void searchRect(Position mp1, int w, int h) {
		screen.setSearchRect(mp1.x, mp1.y, mp1.x + w, mp1.y + h);
	}

	/**
	 * Set search bounds limit, so next search will be performed on limited region,
	 * this will affect on fragment searching time(reduce searching delay) Tip: This
	 * can improve searching speed, if you surely know, where your fragment will
	 * appear
	 * 
	 * @param mp1
	 *            Searching limit bounds (upper left corner)
	 * @param mp2
	 *            Searching limit bounds (bottom right corner)
	 */
	public void searchRect(Position mp1, Position mp2) {
		screen.setSearchRect(mp1.x, mp1.y, mp2.x, mp2.y);
	}

	/**
	 * Set search bounds limit, so next search will be performed on limited region,
	 * this will affect on fragment searching time(reduce searching delay) Tip: This
	 * can improve searching speed, if you surely know, where your fragment will
	 * appear
	 * 
	 * @param mr
	 *            Region searching limit bounds
	 */
	public void searchRect(Region mr) {
		screen.setSearchRect(mr);
	}

	/**
	 * Remove search bounds limit, so next search will be performed on whole screen
	 */
	public void searchRect() {
		screen.setSearchRect();
	}

	/**
	 * Get current search bounds limit
	 * 
	 * @return NULL or Region object
	 */
	public Region getSearchRect() {
		return screen.getSearchRect();
	}

	// //////////////////////END OF FIND
	// METHODS//////////////////////////////////////

	// //////////////KEYBOARD METHODS///////////////////
	/**
	 * Press a single keyboard key
	 * 
	 * @param key_mask
	 *            Key mask, for example KeyEvent.VK_A
	 */
	public void keyPress(int key_mask) {
		robot.keyPress(key_mask);
	}

	/**
	 * Release a single keyboard key
	 * 
	 * @param key_mask
	 *            Key mask, for example KeyEvent.VK_A
	 */
	public void keyRelease(int key_mask) {
		robot.keyRelease(key_mask);
	}

	/**
	 * Press multiple keyboard keys in one method call listed as array or multiple
	 * parameters KeyPress(KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C)
	 * 
	 * @param keys
	 *            Key masks, for example KeyEvent.VK_A
	 */
	public void keyPress(int... keys) {
		for (int key : keys)
			keyPress(key);
	}

	/**
	 * Release multiple keyboard keys in one method call listed as array or multiple
	 * parameters KeyPress(KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C)
	 * 
	 * @param keys
	 *            Key masks, for example KeyEvent.VK_A
	 */
	public void keyRelease(int... keys) {
		for (int key : keys)
			keyRelease(key);
	}

	/**
	 * Perform a click operation on a single keyboard key
	 * 
	 * @param key_mask
	 *            Key mask, for example KeyEvent.VK_A
	 */
	public void keyClick(int key_mask) {
		keyPress(key_mask);
		sleep(keyboardDelay);
		keyRelease(key_mask);
	}

	/**
	 * Perform a click operation on multiple keyboard keys in one method call listed
	 * as array or multiple parameters KeyPress(KeyEvent.VK_A, KeyEvent.VK_B,
	 * KeyEvent.VK_C)
	 * 
	 * @param keys
	 *            Key masks, for example KeyEvent.VK_A
	 */
	public void keyClick(int... keys) {
		keyPress(keys);
		sleep(keyboardDelay);
		keyRelease(keys);
	}

	/**
	 * Write string to system clipboard
	 * 
	 * @param text
	 *            Text being written
	 */
	public void writeClipboard(String text) {
		write(text);
	}

	/**
	 * Get system clipboard TEXT value
	 * 
	 * @return String TEXT value
	 */
	public String readClipboard() {
		return read();
	}

	// ///////////END OF KEYBOARD METHODS//////////////////

	// ///////////SCREEN METHODS///////////////////////
	/**
	 * Grab a screenshot and transform it to int matrix for future fragment
	 * searches, take a full screen image, pretty slow operation
	 * 
	 * @throws Exception
	 *             Exception
	 */
	public void grab() throws Exception {
		screen.grab();
	}

	/**
	 * Grab a screenshot and transform it to int matrix for future fragment
	 * searches, pretty slow operation, speed depends on region size. Tip: Smaller
	 * area being captured faster than huge one, but dependency is not linear
	 * 
	 * @param x1
	 *            Specify capture bounds (upper left corner)
	 * @param y1
	 *            Specify capture bounds (upper left corner)
	 * @param x2
	 *            Specify capture bounds (bottom right corner)
	 * @param y2
	 *            Specify capture bounds (bottom right corner)
	 * @throws Exception
	 *             Exception
	 */
	public void grab(int x1, int y1, int x2, int y2) throws Exception {
		grab(new Rectangle(x1, y1, x2 - x1, y2 - y1));
	}

	/**
	 * Grab a screenshot and transform it to int matrix for future fragment
	 * searches, pretty slow operation, speed depends on region size. Tips: Smaller
	 * area being captured faster than huge one, but dependency is not linear
	 * 
	 * @param mp
	 *            Position to specify capture bounds (upper left corner)
	 * @param w
	 *            width
	 * @param h
	 *            height
	 * @throws Exception
	 *             Exception
	 */
	public void grab(Position mp, int w, int h) throws Exception {
		grab(new Rectangle(mp.x, mp.y, w, h));
	}

	/**
	 * Grab a screenshot and transform it to int matrix for future fragment
	 * searches, pretty slow operation, speed depends on region size. Tips: Smaller
	 * area being captured faster than huge one, but dependency is not linear
	 * 
	 * @param p1
	 *            Position to specify capture bounds (upper left corner)
	 * @param p2
	 *            Position to specify capture bounds (bottom right corner)
	 * @throws Exception
	 *             Exception
	 */
	public void grab(Position p1, Position p2) throws Exception {
		grab(new Rectangle(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y));
	}

	/**
	 * Grab a screenshot and transform it to int matrix for future fragment
	 * searches, pretty slow operation, speed depends on region size. Tips: Smaller
	 * area being captured faster than huge one, but dependency is not linear
	 * 
	 * @param rect
	 *            Rectangle to specify capture bounds
	 * @throws Exception
	 *             Exception
	 */
	public void grab(Rectangle rect) throws Exception {
		screen.grab(rect);
	}

	// ///////////END OF SCREEN METHODS//////////////////

	// /////////////////////////////HELPER PUBLIC
	// METHODS////////////////////////////

	/**
	 * Get current image that is grabbed by grab() method
	 * 
	 * @return BufferedImage that is being stored in memory
	 */
	public BufferedImage screenImage() {
		return screen.getImage();
	}

	/**
	 * Get particular pixel value on the screen
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @return signed 32 bit ARGB (Alpha Red Greeb Blue), 0xFFFFFFFF equals to -1
	 */
	public int screenPixel(int x, int y) {
		return screen.getPixel(x, y);
	}

	/**
	 * Get particular pixel value on the screen as 4 signed bytes
	 * 
	 * @param x
	 *            X axis value
	 * @param y
	 *            Y axis value
	 * @return signed 4 byte ARGB (Alpha Red Greeb Blue), 0xFF equals to -1
	 */
	public byte[] screenPixelARGB(int x, int y) {
		int p = screenPixel(x, y);
		return new byte[] { (byte) (p >>> 24), (byte) (p >>> 16), (byte) (p >>> 8), (byte) p };
	}

	/**
	 * Get current mouse delay for mouseClick and findClick operations,
	 * press-release delay
	 * 
	 * @return Current mouse delay in milliseconds, 1 second = 1000 milliseconds
	 */
	public int getMouseDelay() {
		return mouseDelay;
	}

	/**
	 * Specify mouse delay for mouseClick and findClick operations, press-release
	 * delay
	 * 
	 * @param mouseDelay
	 *            Delay in milliseconds, 1 second = 1000 milliseconds
	 */
	public void setMouseDelay(int mouseDelay) {
		this.mouseDelay = mouseDelay;
	}

	/**
	 * Get current keyboard delay for keyClick operations, press-release delay
	 * 
	 * @return Current keyboard delay in milliseconds, 1 second = 1000 milliseconds
	 */
	public int getKeyboardDelay() {
		return keyboardDelay;
	}

	/**
	 * Specify keyboard delay for keyClick operations, press-release delay
	 * 
	 * @param keyboardDelay
	 *            Delay in milliseconds, 1 second = 1000 milliseconds
	 */
	public void setKeyboardDelay(int keyboardDelay) {
		this.keyboardDelay = keyboardDelay;
	}

	/**
	 * Returns core version in x.x.x format
	 * 
	 * @return Version string
	 */

	public String getVersion() {
		return Update.version;
	}

	/**
	 * Manually reload fragments if you create fragments from a script at run
	 */
	public void reloadFrags() {
		Data.loadFragments(false); // console log turned off by false param
	}

	/**
	 * Create a fragment from script, call reloadFrags() after this method
	 * 
	 * @param name
	 *            Regular name for a new fragment
	 * @param mr
	 *            Region to specify fragment bounds
	 * @throws Exception
	 *             Exception
	 */
	public void createFrag(String name, Region mr) throws Exception {
		createFrag(name, mr.p1.x, mr.p1.y, mr.p2.x, mr.p2.y);
	}

	/**
	 * Create a fragment from script, call reloadFrags() after this method
	 * 
	 * @param name
	 *            Regular name for a new fragment
	 * @param p1
	 *            Position to specify fragment bounds (upper left corner)
	 * @param p2
	 *            Position to specify fragment bounds (bottom right corner)
	 * @throws Exception
	 *             Exception
	 */
	public void createFrag(String name, Position p1, Position p2) throws Exception {
		createFrag(name, p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Create a fragment from script, call reloadFrags() after this method
	 * 
	 * @param name
	 *            Regular name for a new fragment
	 * @param x1
	 *            Specify fragment bounds (upper left corner)
	 * @param y1
	 *            Specify fragment bounds (upper left corner)
	 * @param x2
	 *            Specify fragment bounds (bottom right corner)
	 * @param y2
	 *            Specify fragment bounds (bottom right corner)
	 * @throws Exception
	 *             Exception
	 */
	public void createFrag(String name, int x1, int y1, int x2, int y2) throws Exception {
		createFrag(name, x1, y1, x2 - x1, y2 - y1);
	}

	/**
	 * Create a fragment from script, call reloadFrags() after this method
	 * 
	 * @param name
	 *            Regular name for a new fragment
	 * @param p1
	 *            Position to specify fragment bounds (upper left corner)
	 * @param w
	 *            Fragment width
	 * @param h
	 *            Fragment height
	 * @throws Exception
	 *             Exception
	 */
	public void createFrag(String name, Position p1, int w, int h) throws Exception {
		new Frag(screenImage().getSubimage(p1.x, p1.y, w, h)).makeFile(name);
	}

	/**
	 * 
	 * @return List of loaded fragment names
	 */
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

	private Clipboard getSystemClipboard() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		Clipboard systemClipboard = defaultToolkit.getSystemClipboard();
		return systemClipboard;
	}
}
