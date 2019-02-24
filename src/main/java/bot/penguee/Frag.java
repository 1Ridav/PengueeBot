package bot.penguee;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Frag {
	protected int[][] rgbData = null;
	protected BufferedImage image = null;

	Frag() {

	}

	Frag(String path) throws Exception {
		File f = new File(path);
		image = ImageIO.read(f);
		rgbData = loadFromFile(image);
	}

	public Frag(BufferedImage bi) throws Exception {
		image = bi;
		getIntRGB(bi);
	}

	Frag(int[][] rgb) throws Exception {
		rgbData = rgb;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int[][] getRgbData() {
		return rgbData;
	}

	public int getWidth() {
		return rgbData[0].length;
	}

	public int getHeight() {
		return rgbData.length;
	}

	public Position center() {
		return new Position(getWidth() / 2, getHeight() / 2);
	}

	// USED FOR ROBOT SCREENSHOT BUFFERED_IMAGE
	int[][] getIntRGB(BufferedImage image) {
		final int[] pixels = ((DataBufferInt) image.getData().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		this.image = image;
		if (rgbData == null)
			rgbData = new int[height][width];
		for (int i = 0; i < height; i++)
			System.arraycopy(pixels, (i * width), rgbData[i], 0, width);
		return rgbData;
	}

	int[][] getIntRGB(BufferedImage image, int x, int y) {
		final int[] pixels = ((DataBufferInt) image.getData().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		for (int i = 0; i < height; i++)
			System.arraycopy(pixels, (i * width), rgbData[i + y], x, width);

		return rgbData;
	}

	protected int[][] loadFromFile(BufferedImage image) throws Exception {
		final byte[] pixels = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		final int width = image.getWidth();
		image.getType();

		if (rgbData == null)
			rgbData = new int[image.getHeight()][width];

		switch (image.getType()) {
		case BufferedImage.TYPE_3BYTE_BGR: // BMP files
			for (int pixel = 0, row = 0; pixel < pixels.length; row++)
				for (int col = 0; col < width; col++, pixel += 3)
					rgbData[row][col] = 0xFF000000 + ((int) pixels[pixel] & 0xFF)
							+ (((int) pixels[pixel + 1] & 0xFF) << 8) + (((int) pixels[pixel + 2] & 0xFF) << 16);
			break;
		case BufferedImage.TYPE_4BYTE_ABGR: // PNG
			for (int pixel = 0, row = 0; pixel < pixels.length; row++)
				for (int col = 0; col < width; col++, pixel += 4)
					// IF ALPHA IS BIGGER THAN 0, THEN SET TO 255, ELSE TRANSPARENT
					rgbData[row][col] = 0xFF000000 + ((int) pixels[pixel + 1] & 0xFF)
							+ (((int) pixels[pixel + 2] & 0xFF) << 8) + (((int) pixels[pixel + 3] & 0xFF) << 16);

			break;
		default:
			throw new Exception();
		}
		return rgbData;
	}

	public void makeFile(String name) throws Exception {
		File f = new File(Data.resourcesPath);
		name = name.trim();
		String s;
		if (name.contains(".")) {
			s = name.substring(0, name.lastIndexOf("."));
			s = s.replace(".", File.separator);
		} else {
			s = "";
		}
		File ff = new File(f.getAbsolutePath() + File.separator + s,
				name.substring(name.lastIndexOf(".") + 1, name.length()) + ".png");
		ff.mkdirs();
		ImageIO.write(image, "png", ff);
	}

	public Position findSimilarIn(Frag b, double rate, int x_start, int y_start, int x_stop, int y_stop) {
		// precalculate all frequently used data
		final int[][] small = this.rgbData;
		final int[][] big = b.rgbData;
		final int small_height = small.length;
		final int small_width = small[0].length;
		final long maxDiff = 3 * 255 * small_height * small_width;
		// similarity rate 95% is equal to 5% difference rate.
		// if differences reached this number, then no need to check the rest, continue
		// to next position
		final long maxBreakDiff = (long) ((1 - rate) * maxDiff);
		long leastDifference = Long.MAX_VALUE;
		Position bestResultMatrixPosition = null;

		int[] row_cache_big = null;
		int[] row_cache_small = null;
		for (int y = y_start; y < y_stop; y++) {
			__columnscan: for (int x = x_start; x < x_stop; x++) {
				long diff = 0;// sum difference values
				for (int yy = 0; yy < small_height; yy++) {
					row_cache_small = small[yy];
					row_cache_big = big[y + yy];
					for (int xx = 0; xx < small_width; xx++) {
						diff += pixelDiffARGB(row_cache_big[x + xx], row_cache_small[xx]);
						if (diff > maxBreakDiff)
							continue __columnscan; // no match
					}
				}

				if (diff == 0)
					return new Position(x, y); // full match
				else if (diff < leastDifference) { // found better match
					leastDifference = diff;
					bestResultMatrixPosition = new Position(x, y);
				}
			}
		}
		return bestResultMatrixPosition;
	}

	private static int pixelDiffARGB(int rgb1, int rgb2) {// A channel being ignored
		return abs(((rgb1 >> 16) & 0xff) - ((rgb2 >> 16) & 0xff)) + abs(((rgb1 >> 8) & 0xff) - ((rgb2 >> 8) & 0xff))
				+ abs((rgb1 & 0xff) - (rgb2 & 0xff));
	}

	private static int abs(int i) {
		return (i + (i >> 31)) ^ (i >> 31);
	}

	public Position findIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		// precalculate all frequently used data
		final int[][] small = this.rgbData;
		final int[][] big = b.rgbData;
		final int small_height = small.length;
		final int small_width = small[0].length;
		final int small_height_minus_1 = small_height - 1;
		final int small_width_minus_1 = small_width - 1;
		final int first_pixel = small[0][0];
		final int last_pixel = small[small_height_minus_1][small_width_minus_1];

		int[] row_cache_big = null;
		int[] row_cache_big2 = null;
		int[] row_cache_small = null;
		for (int y = y_start; y < y_stop; y++) {
			row_cache_big = big[y];
			__columnscan: for (int x = x_start; x < x_stop; x++) {
				if (row_cache_big[x] != first_pixel
						|| big[y + small_height_minus_1][x + small_width_minus_1] != last_pixel)
					continue __columnscan; // No first match

				// There is a match for the first element in small
				// Check if all the elements in small matches those in big
				for (int yy = 0; yy < small_height; yy++) {
					row_cache_big2 = big[y + yy];
					row_cache_small = small[yy];
					for (int xx = 0; xx < small_width; xx++) {
						// If there is at least one difference, there is no
						// match
						if (row_cache_big2[x + xx] != row_cache_small[xx]) {
							continue __columnscan;
						}
					}
				}
				// If arrived here, then the small matches a region of big
				return new Position(x, y);
			}
		}
		return null;
	}

	public Position[] findAllIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		// precalculate all frequently used data
		ArrayList<Position> result = null;
		final int[][] small = this.rgbData;
		final int[][] big = b.rgbData;
		final int small_height = small.length;
		final int small_width = small[0].length;
		final int small_height_minus_1 = small_height - 1;
		final int small_width_minus_1 = small_width - 1;
		final int first_pixel = small[0][0];
		final int last_pixel = small[small_height_minus_1][small_width_minus_1];

		int[] row_cache_big = null;
		int[] row_cache_big2 = null;
		int[] row_cache_small = null;
		for (int y = y_start; y < y_stop; y++) {
			row_cache_big = big[y];
			__columnscan: for (int x = x_start; x < x_stop; x++) {
				if (row_cache_big[x] != first_pixel
						|| big[y + small_height_minus_1][x + small_width_minus_1] != last_pixel)
					continue __columnscan; // No first match
				// There is a match for the first element in small
				// Check if all the elements in small matches those in big
				for (int yy = 0; yy < small_height; yy++) {
					row_cache_big2 = big[y + yy];
					row_cache_small = small[yy];
					for (int xx = 0; xx < small_width; xx++)
						// If there is at least one difference, there is no
						// match
						if (row_cache_big2[x + xx] != row_cache_small[xx]) {
							continue __columnscan;
						}
				}
				// If arrived here, then the small matches a region of big
				if (result == null)
					result = new ArrayList<Position>();
				result.add(new Position(x, y));

			}
		}

		if (result != null) {
			return (Position[]) result.toArray(new Position[0]);
		}

		return null;
	}
}
