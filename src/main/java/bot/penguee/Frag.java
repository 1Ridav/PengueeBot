package bot.penguee;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Frag {
	// protected Set hashes = new HashSet();
	protected int[][] rgbData = null;
	protected BufferedImage image = null;

	Frag(String file) throws Exception {

		image = ImageIO.read(new File(file));
		rgbData = loadFromFile(image);

		/*
		 * for (int i = 0; i < image.getHeight(); i++) { hashes.add(hash(rgbData, 0, i,
		 * rgbData[0].length)); }
		 */
	}

	public Frag(BufferedImage bi) throws Exception {
		image = bi;
		getIntRGB(bi);
		/*
		 * for (int i = 0; i < image.getHeight(); i++) { hashes.add(hash(rgbData, 0, i,
		 * rgbData[0].length)); }
		 */
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
		return new MatrixPosition(getWidth() / 2, getHeight() / 2);
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

	// USED FOR BMP/PNG BUFFERED_IMAGE
	private int[][] loadFromFile(BufferedImage image) {
		final byte[] pixels = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		final int width = image.getWidth();

		if (rgbData == null)
			rgbData = new int[image.getHeight()][width];

		for (int pixel = 0, row = 0; pixel < pixels.length; row++)
			for (int col = 0; col < width; col++, pixel += 3)
				rgbData[row][col] = -16777216 + ((int) pixels[pixel] & 0xFF) + (((int) pixels[pixel + 1] & 0xFF) << 8)
						+ (((int) pixels[pixel + 2] & 0xFF) << 16); // 255
																	// alpha, r
																	// g b;

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
				name.substring(name.lastIndexOf(".") + 1, name.length()) + ".bmp");
		ff.mkdirs();
		ImageIO.write(image, "bmp", ff);
	}

	/*
	 * private long hash(int[][] b, int x, int y, int len) { long h = 0; int[] c =
	 * b[y]; int stop = x + len; for (int i = x; i < stop; i++) h = 33 * h + c[i];
	 * 
	 * return h; }
	 */
	/*
	 * public MatrixPosition findIn(Frag b, int x_start, int y_start, int x_stop,
	 * int y_stop) { // return null; y_stop += getHeight() + 1; // x_stop +=
	 * getHeight() + 1;
	 * 
	 * final int[][] small = this.rgbData; final int[][] big = b.rgbData; final int
	 * small_height = small.length; final int small_width = small[0].length; final
	 * int small_height_minus_1 = small_height - 1; final int first_pixel =
	 * small[0][0]; int[] cache; int[] cache_small; for (int y = y_start +
	 * small_height_minus_1; y < y_stop; y += small_height) { //long h = hash(big,
	 * x_start, y, small_width); for (int x = x_start; x < x_stop; x++) { if
	 * (hashes.contains(hash(big, x_start, y, small_width))) { //
	 * System.out.println("partial found at " + j + " " + i); gonext2: for (int l =
	 * small_height_minus_1; l >= 0; l--) { if (big[y - l][x] == first_pixel) { for
	 * (int yy = 0; yy < small_height; yy++) { cache = big[y + yy - l]; cache_small
	 * = small[yy]; for (int xx = 0; xx < small_width; xx++) if (cache[x + xx] !=
	 * cache_small[xx]) continue gonext2; } // System.out.println("MATCH!"); return
	 * new MatrixPosition(x, y - l); }
	 * 
	 * } } //h -= big[y][x]; //h += big[y][x + small_width]; } }
	 * 
	 * return null; }
	 */

	public MatrixPosition findSimilarIn(Frag b, double rate, int x_start, int y_start, int x_stop, int y_stop) {
		// precalculate all frequently used data
		final int[][] small = this.rgbData;
		final int[][] big = b.rgbData;
		final int small_height = small.length;
		final int small_width = small[0].length;
		rate = 100 - rate;// similarity rate 95% is qual to 5% difference rate.
		final long maxDiff = 3 * 255 * small_height * small_width;
		final long maxBreakDiff = (long) ((rate / 100) * maxDiff);
		long leastDifference = Long.MAX_VALUE;
		MatrixPosition bestResultMatrixPosition = null;

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
					return new MatrixPosition(x, y); // full match
				else if (diff < leastDifference) { // found better match
					leastDifference = diff;
					bestResultMatrixPosition = new MatrixPosition(x, y);
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

	public MatrixPosition findIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
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

	public MatrixPosition[] findAllIn(Frag b, int x_start, int y_start, int x_stop, int y_stop) {
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
						|| big[y + small_height_minus_1][x + small_width_minus_1] != last_pixel)
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
