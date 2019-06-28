package bot.penguee.fragments;

import java.awt.image.BufferedImage;

import bot.penguee.Position;

public interface FragmentInterface {
	public BufferedImage getImage();

	public int getWidth();

	public int getHeight();

	public Position center();

	public int[][] getRgbData();

	public int[][] getIntRGB(BufferedImage image);

	public int[][] getIntRGB(BufferedImage image, int x, int y);

	int[][] loadFromFile(BufferedImage image) throws Exception;

	public void makeFile(String name) throws Exception;

	public Position findSimilarIn(Frag b, double rate, int x_start, int y_start, int x_stop, int y_stop);

	public Position findIn(Frag b, int x_start, int y_start, int x_stop, int y_stop);

	public Position[] findAllIn(Frag b, int x_start, int y_start, int x_stop, int y_stop);
}
