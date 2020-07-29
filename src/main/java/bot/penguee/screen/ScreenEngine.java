package bot.penguee.screen;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import bot.penguee.Data;
import bot.penguee.Position;
import bot.penguee.Region;
import bot.penguee.exception.FragmentNotLoadedException;
import bot.penguee.exception.ScreenNotGrabbedException;
import bot.penguee.screen.cpu.Screen;
import bot.penguee.screen.gpu.ScreenGPU;

public class ScreenEngine implements ScreenEngineInterface {

	private ScreenEngineInterface se;

	public ScreenEngine() {
		se = Data.getForceUseGPU() ? new ScreenGPU() : new Screen();
	}

	@Override
	public Position[] find_all(String fragName) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return se.find_all(fragName);
	}

	@Override
	public Position[] find_all(String fragName, String customPosName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return se.find_all(fragName, customPosName);
	}

	@Override
	public Position find(String fragName, String customPosName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return se.find(fragName, customPosName);
	}

	@Override
	public Position findSimilar(String fragName, double rate, String customName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		return se.findSimilar(fragName, rate, customName);
	}

	@Override
	public Region getSearchRect() {
		return se.getSearchRect();
	}

	@Override
	public boolean getSearchInRegion() {
		return se.getSearchInRegion();
	}

	@Override
	public Rectangle getRect() {
		return se.getRect();
	}

	@Override
	public void setSearchRect(int x1, int y1, int x2, int y2) {
		se.setSearchRect(x1, y1, x2, y2);
	}

	@Override
	public void setSearchRect(Region mr) {
		se.setSearchRect(mr);
	}

	@Override
	public void setSearchRect() {
		se.setSearchRect();
	}

	@Override
	public void grab() throws Exception {
		se.grab();
	}

	@Override
	public void grab(Rectangle rect) throws Exception {
		se.grab(rect);
	}

	@Override
	public BufferedImage getImage() {
		return se.getImage();
	}

	@Override
	public long getPixel(int x, int y) {
		return se.getPixel(x, y);
	}

	@Override
	public void setSearchRect(Position p1, Position p2) {
		// TODO Auto-generated method stub
		se.setSearchRect(p1,p2);
	}

}
