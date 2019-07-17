package bot.penguee.fragments;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import bot.penguee.Position;

public class FragMono extends Frag {
	private int monoColor = 0;
	private int[] x_map = null, y_map = null;// best for performance, but not much readable usage
	private final int height, width;

	public FragMono(String file) throws Exception {
		super(file);
		type = FragmentInterface.type.mono;
		String color = file.substring(file.lastIndexOf("((") + 2, file.lastIndexOf("))"));
		monoColor = Integer.parseInt(color);
		prepareMonoPixelMap();
		this.height = rgbData.length;
		this.width = rgbData[0].length;
		this.rgbData = null;
	}

	public FragMono(BufferedImage image, int color) throws Exception {
		super(image);
		type = FragmentInterface.type.mono;
		monoColor = color;
		prepareMonoPixelMap();

		// we don't need rgbData matrix anymore
		this.height = rgbData.length;
		this.width = rgbData[0].length;
		this.rgbData = null;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void makeFile(String name) throws Exception {
		super.makeFile(new StringBuilder(name).append("_((").append(monoColor).append("))").toString());
	}

	private void prepareMonoPixelMap() {
		int[] row_cache = null;
		final int line = rgbData.length;
		final int column = rgbData[0].length;
		ArrayList<Point> al = new ArrayList<Point>();

		for (int y = 0; y < line; y++) {
			row_cache = rgbData[y];
			for (int x = 0; x < column; x++)
				if (row_cache[x] == monoColor)
					al.add(new Point(x, y));
		}
		// convert array of objects to an array of primitives to make it more cache
		// friendly
		x_map = new int[al.size()];
		y_map = new int[al.size()];
		for (int i = 0; i < al.size(); i++) {
			Point p = al.get(i);
			x_map[i] = (int) p.x;
			y_map[i] = (int) p.y;
		}
	}

	@Override
	public Position findSimilarIn(Frag b, double rate, int x_start, int y_start, int x_stop, int y_stop) {
		return null;// This method is not implemented due to this has no meaning. Mono fragments use
					// exact matching
	}

	@Override
	public Position findIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		final int[][] big = b.rgbData;
		final int[] rowY = y_map;
		final int[] rowX = x_map;
		final int jumpX = rowX[0];
		final int jumpY = rowY[0];
		final int first_pixel = monoColor;
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
					if (big[y + rowY[yy]][x + rowX[yy]] != first_pixel)
						continue __columnscan;
				return new Position(x, y);
			}
		}
		return null;
	}

	@Override
	public Position[] findAllIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		final int[][] big = b.rgbData;
		final int[] rowY = y_map;
		final int[] rowX = x_map;
		final int jumpX = rowX[0];
		final int jumpY = rowY[0];
		final int first_pixel = monoColor;
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
					if (big[y + rowY[yy]][x + rowX[yy]] != first_pixel)
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
