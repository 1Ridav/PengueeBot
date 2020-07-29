package bot.penguee.screen;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import bot.penguee.Position;
import bot.penguee.Region;
import bot.penguee.exception.FragmentNotLoadedException;
import bot.penguee.exception.ScreenNotGrabbedException;

public interface ScreenEngineInterface {

	Position[] find_all(String fragName) throws FragmentNotLoadedException, ScreenNotGrabbedException;
	
	Position[] find_all(String fragName, String customPosName) throws FragmentNotLoadedException, ScreenNotGrabbedException;

	Position find(String fragName, String customPosName) throws FragmentNotLoadedException, ScreenNotGrabbedException;

	Position findSimilar(String fragName, double rate, String customName) throws FragmentNotLoadedException, ScreenNotGrabbedException;

	Region getSearchRect();

	boolean getSearchInRegion();

	Rectangle getRect();

	void setSearchRect(int x1, int y1, int x2, int y2);
	
	void setSearchRect(Position p1, Position p2);

	void setSearchRect(Region mr);

	void setSearchRect();

	void grab() throws Exception;

	void grab(Rectangle rect) throws Exception;

	BufferedImage getImage();

	long getPixel(int x, int y);


}
