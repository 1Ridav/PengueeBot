package bot.penguee;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class FragMono extends Frag {
	private int monoColor = 0;
	private int[][] monoXY_Map = null;

	FragMono(String file) throws Exception {
		super(file);
		String color = file.substring(file.lastIndexOf("((") + 2, file.lastIndexOf("))"));
		monoColor = Integer.parseInt(color);
		prepareMonoPixelMap();
	}

	public FragMono(BufferedImage image, int color) throws Exception {
		super(image);
		monoColor = color;
		prepareMonoPixelMap();
	}

	public void makeFile(String name) throws Exception {
		super.makeFile(name + "_((" + monoColor + "))");
	}

	private void prepareMonoPixelMap() {
		int[] row_cache = null;
		final int line = rgbData.length;
		final int column = rgbData[0].length;
		ArrayList<Point> al = new ArrayList<Point>();

		// Point first = null;

		for (int y = 0; y < line; y++) {
			row_cache = rgbData[y];
			for (int x = 0; x < column; x++)
				if (row_cache[x] == monoColor)
					al.add(new Point(x, y));
		}

		monoXY_Map = new int[2][al.size()];
		for (int i = 0; i < al.size(); i++) {
			Point p = al.get(i);
			monoXY_Map[0][i] = (int) p.getX();
			monoXY_Map[1][i] = (int) p.getY();
		}
	}

	public Position findSimilarIn(FragTransparent b, double rate, int x_start, int y_start, int x_stop, int y_stop) {
		return null;
	}

	@Override
	public Position findIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		final int[][] big = b.rgbData;
		final int[] monoY = monoXY_Map[1];
		final int[] monoX = monoXY_Map[0];
		final int jumpX = monoX[0];
		final int jumpY = monoY[0];
		final int first_pixel = monoColor;
		final int pixelMapLen = monoX.length;

		int[] row_cache_big = null;
		for (int y = y_start; y < y_stop; y++) {
			row_cache_big = big[y + jumpY];
			__columnscan: for (int x = x_start; x < x_stop; x++) {
				if (row_cache_big[x + jumpX] != first_pixel)
					continue __columnscan; // No first match
				// There is a match for the first element in small
				// Check if all the elements in small matches those in big
				for (int yy = 0; yy < pixelMapLen; yy++)
					if (big[y + monoY[yy]][x + monoX[yy]] != first_pixel)
						continue __columnscan;
				return new Position(x, y);
			}
		}
		return null;
	}

	@Override
	public Position[] findAllIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
		final int[][] big = b.rgbData;
		final int[] monoY = monoXY_Map[1];
		final int[] monoX = monoXY_Map[0];
		final int jumpX = monoX[0];
		final int jumpY = monoY[0];
		final int first_pixel = monoColor;
		final int pixelMapLen = monoX.length;

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
					if (big[y + monoY[yy]][x + monoX[yy]] != first_pixel)
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
