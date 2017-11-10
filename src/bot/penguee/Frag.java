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

	Frag(String file) throws Exception {

		image = ImageIO.read(new File(file));
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
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public MatrixPosition center() {
		return new MatrixPosition(rgbData[0].length / 2, rgbData.length / 2);
	}

	// USED FOR ROBOT SCREENSHOT BUFFERED_IMAGE
	int[][] getIntRGB(BufferedImage image) {
		final int[] pixels = ((DataBufferInt) image.getData().getDataBuffer())
				.getData();
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
		final int[] pixels = ((DataBufferInt) image.getData().getDataBuffer())
				.getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		for (int i = 0; i < height; i++)
			System.arraycopy(pixels, (i * width), rgbData[i + y], x, width);

		return rgbData;
	}

	// USED FOR BMP/PNG BUFFERED_IMAGE
	private int[][] loadFromFile(BufferedImage image) {
		final byte[] pixels = ((DataBufferByte) image.getData().getDataBuffer())
				.getData();
		final int width = image.getWidth();

		if (rgbData == null)
			rgbData = new int[image.getHeight()][width];

		int argb = 0;

		final int pixelLength = 3;
		for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
			argb = -16777216; // 255 alpha
			argb += ((int) pixels[pixel] & 0xFF); // blue
			argb += (((int) pixels[pixel + 1] & 0xFF) << 8); // green
			argb += (((int) pixels[pixel + 2] & 0xFF) << 16); // red
			rgbData[row][col] = argb;
			col++;
			if (col == width) {
				col = 0;
				row++;
			}
		}

		return rgbData;
	}

	public void makeFile(String name) throws Exception {
		File f = new File(Data.resourcesPath);
		String s = name.substring(0, name.lastIndexOf("."));
		s = s.replace(".", File.separator);
		File ff = new File(f.getAbsolutePath() + File.separator + s,
				name.substring(name.lastIndexOf(".") + 1, name.length())
						+ ".bmp");
		ff.mkdirs();
		ImageIO.write(image, "bmp", ff);
	}

	public MatrixPosition findIn(Frag b, int x_start, int y_start, int x_stop,
			int y_stop) {
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
						|| big[y + small_height_minus_1][x
								+ small_width_minus_1] != last_pixel)
					// if (row_cache_big[x] != first_pixel)
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
				return new MatrixPosition(x, y);
			}
		}
		return null;
	}

	public MatrixPosition[] findAllIn(Frag b, int x_start, int y_start,
			int x_stop, int y_stop) {
		// precalculate all frequently used data
		ArrayList<MatrixPosition> result = null;
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
						|| big[y + small_height_minus_1][x
								+ small_width_minus_1] != last_pixel)
					// if (row_cache_big[x] != first_pixel)
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
					result = new ArrayList<MatrixPosition>();
				// MatrixPosition result = new MatrixPosition();
				result.add(new MatrixPosition(x, y));

			}
		}

		if (result != null) {
			return (MatrixPosition[]) result.toArray(new MatrixPosition[0]);
		}

		return null;
	}

}
