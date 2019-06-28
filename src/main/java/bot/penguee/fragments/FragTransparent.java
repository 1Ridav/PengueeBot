package bot.penguee.fragments;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import bot.penguee.Position;

public class FragTransparent extends Frag {
	private int[] x_map = null, y_map = null; // much faster than using array of objects

	public FragTransparent(String path) throws Exception {
		super();
		File f = new File(path);
		image = ImageIO.read(f);
		rgbData = loadFromFile(image);
		preparePixelMap();
	}

	private void preparePixelMap() {
		int[] row_cache = null;
		final int line = rgbData.length;
		final int column = rgbData[0].length;
		ArrayList<Point> al = new ArrayList<Point>();

		// Point first = null;

		for (int y = 0; y < line; y++) {
			row_cache = rgbData[y];
			for (int x = 0; x < column; x++)
				if (row_cache[x] != 0)
					al.add(new Point(x, y));
		}

		x_map = new int[al.size()];
		y_map = new int[al.size()];
		for (int i = 0; i < al.size(); i++) {
			Point p = al.get(i);
			x_map[i] = (int) p.getX();
			y_map[i] = (int) p.getY();
		}
	}

	@Override
	public int[][] loadFromFile(BufferedImage image) throws Exception {
		final byte[] pixels = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		final int width = image.getWidth();
		image.getType();

		if (rgbData == null)
			rgbData = new int[image.getHeight()][width];

		switch (image.getType()) {
		case BufferedImage.TYPE_4BYTE_ABGR: // PNG
			for (int pixel = 0, row = 0; pixel < pixels.length; row++)
				for (int col = 0; col < width; col++, pixel += 4) {
					// SET ALPHA TO 255, NO TRANSPARENCY
					if (pixels[pixel] != -1) { // not 255(0xFF), then it is transparent
						rgbData[row][col] = 0;
					} else {
						rgbData[row][col] = (int) 0xFF000000 + ((int) pixels[pixel + 1] & 0xFF)
								+ (((int) pixels[pixel + 2] & 0xFF) << 8) + (((int) pixels[pixel + 3] & 0xFF) << 16);
					}
				}
			break;
		default:
			throw new Exception();
		}
		return rgbData;
	}

	@Override
	public void makeFile(String name) throws Exception {
		super.makeFile(name + "_((TRANSPARENT))");
	}

	@Override
	public Position findSimilarIn(Frag b, double rate, int x_start, int y_start, int x_stop, int y_stop) {
		// precalculate all frequently used data
		final int[][] small = this.rgbData;
		final int[][] big = b.rgbData;
		final int[] rowY = y_map;
		final int[] rowX = x_map;
		final int pixelMapLen = rowX.length;

		final long maxDiff = 3 * 255 * pixelMapLen;
		// similarity rate 95% is equal to 5% difference rate.
		// if differences reached this number, then no need to check the rest, continue
		// to next position
		final long maxBreakDiff = (long) ((1 - rate) * maxDiff);

		for (int y = y_start; y < y_stop; y++) {
			__columnscan: for (int x = x_start; x < x_stop; x++) {
				long diff = 0;// sum difference values
				for (int yy = 0; yy < pixelMapLen; yy++)
					diff += pixelDiffARGB(big[y + rowY[yy]][x + rowX[yy]], small[rowY[yy]][rowX[yy]]);
				if (diff > maxBreakDiff)
					continue __columnscan; // no match

				return new Position(x, y);
			}
		}
		return null;
	}

	@Override
	public Position findIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		final int[][] big = b.rgbData;
		final int[][] small = rgbData;
		final int[] rowY = y_map;
		final int[] rowX = x_map;
		final int jumpX = rowX[0];
		final int jumpY = rowY[0];
		final int first_pixel = small[jumpY][jumpX];
		final int pixelMapLen = rowX.length;

		int[] row_cache_big = null;
		for (int y = y_start; y < y_stop; y++) {
			row_cache_big = big[y + jumpY];
			__columnscan: for (int x = x_start; x < x_stop; x++) {
				if (row_cache_big[x + jumpX] != first_pixel)
					continue __columnscan; // No first match
				// There is a match for the first element in small
				// Check if all the elements in small matches those in big
				for (int yy = 0; yy < pixelMapLen; yy++)
					if (big[y + rowY[yy]][x + rowX[yy]] != small[rowY[yy]][rowX[yy]])
						continue __columnscan;
				return new Position(x, y);
			}
		}
		return null;
	}

	@Override
	public Position[] findAllIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		// the idea is to jump according to the
		// transparency map. check only non transparent pixels, assume transparent
		// pixels as a match
		final int[][] big = b.rgbData;
		final int[][] small = rgbData;
		final int[] rowY = y_map;
		final int[] rowX = x_map;
		final int jumpX = rowX[0];
		final int jumpY = rowY[0];
		final int first_pixel = small[jumpY][jumpX];

		final int pixelMapLen = rowX.length;

		ArrayList<Position> result = null;
		Position matrix_position_list[] = null;
		int[] row_cache_big = null;
		for (int y = y_start; y < y_stop; y++) {
			row_cache_big = big[y + jumpY];
			__columnscan: for (int x = x_start; x < x_stop; x++) {
				if (row_cache_big[x + jumpX] != first_pixel)
					continue __columnscan; // No first match
				// There is a match for the first element in small
				// Check if all the elements in small matches those in big
				for (int yy = 0; yy < pixelMapLen; yy++)
					if (big[y + rowY[yy]][x + rowX[yy]] != small[rowY[yy]][rowX[yy]])
						continue __columnscan;
				// If arrived here, then the small matches a region of big
				if (result == null)
					result = new ArrayList<Position>();
				result.add(new Position(x, y));
			}
		}
		if (result != null) {
			matrix_position_list = result.toArray(new Position[0]);
		}
		return matrix_position_list;
	}
}
