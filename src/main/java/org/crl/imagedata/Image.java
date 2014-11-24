package org.crl.imagedata;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
/**
 * Supports BMP, JPG and PNG images.
 **/
public class Image {

	/** The inner image. */
	private BufferedImage image;

	/**
	 * Supports BMP, JPG and PNG images.
	 *
	 * @param pathname the pathname
	 */
	public Image(String pathname) {
		File imageFile = new File(pathname);
		loadImageFromFile(imageFile);

	}

	/**
	 * Implements a blurring filter through using an appropriate transformation matrix.
	 *
	 */
	public Image blur() {
		BufferedImage result = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		float data[] = { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f,
				0.0625f, 0.125f, 0.0625f };
		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
				null);
		convolve.filter(this.image, result);
		return new Image(result);
	}

	/**
	 * Gets the inner image.
	 *
	 * @return the inner image
	 */
	public BufferedImage getInnerImage() {
		return image;
	}

	/**
	 * @return the duplicated image
	 */
	public Image duplicate() {
		Image result;
		BufferedImage duplicated = new BufferedImage(image.getWidth() * 2,
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = duplicated.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(image, image.getWidth(), 0, null);
		Image duplicatedImage = new Image(duplicated);

		return duplicatedImage;
	}

	/**
	 * Load image from file.
	 *
	 * @param imageFile the image file
	 */
	private void loadImageFromFile(File imageFile) {
		try {
			image = ImageIO.read(imageFile);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to read input image");
		}
	}

	/**
	 * Instantiates a new image.
	 *
	 * @param image the image
	 */
	public Image(BufferedImage image) {
		this.image = image;
	}

	/**
	 * Scales the image the desired number of times.
	 * be careful with this since zooming consumes a lot of memory
	 * Zooming in more than 8 times rarely improves OCR recognition rate
	 * When you pass 1 as a param this will zoom in the image 1x times
	 * @param scaleFactor the scale factor
	 * @return the image
	 */
	public Image scale(int scaleFactor) {

		// scales scaleFactor times
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage resultBuffer = new BufferedImage(width * scaleFactor,
				height * scaleFactor, BufferedImage.TYPE_INT_ARGB);
		AffineTransform transformation = new AffineTransform();
		transformation.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(transformation,
				AffineTransformOp.TYPE_BILINEAR);
		resultBuffer = scaleOp.filter(image, resultBuffer);
		Image result = new Image(resultBuffer);
		return result;
	}

	/**
	 * Scale an image a default number of 7 times to increase recognition rate.
	 *
	 * @return the image
	 */
	public Image scale() {
		final int scaleFactor = 7;
		return scale(scaleFactor);
	}

	/**
	 * Orientation representation is Height, Width 0,0 is the Top Left Point.
	 *
	 * @param xCoord the x coord
	 * @param yCoord the y coord
	 * @return the pixel
	 */
	public Pixel getPixel(int xCoord, int yCoord) {
		int[] color = image.getRaster().getPixel(xCoord,
				image.getHeight() - 1 - yCoord, new int[4]);
		Color pixelColor = new Color(color);
		Pixel pixel = new Pixel(pixelColor, xCoord, yCoord);
		return pixel;
	}

	/**
	 * Gets the pixel at the desired point.
	 *
	 * @param point the point
	 * @return the pixel
	 */
	public Pixel getPixel(Point point) {
		int xCoord = point.getXCoord();
		int yCoord = point.getYCoord();
		return getPixel(xCoord, yCoord);
	}

	/**
	 * Monochromize-transforms the image into a two-colors one- very light colors are White, darker are black.
	 *
	 * @return the image
	 */
	public Image monochromize() {
		BufferedImage binarized = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

		int red, green, blue;
		int newPixel;
		int threshold = 230;

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {

				// Get pixels
				red = new java.awt.Color(image.getRGB(i, j)).getRed();
				int alpha = new java.awt.Color(image.getRGB(i, j)).getAlpha();

				if (red > threshold) {
					newPixel = 255;
				} else {
					newPixel = 0;
				}
				newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);
				binarized.setRGB(i, j, newPixel);

			}
		}
		Image result = new Image(binarized);
		return result;
	}

	/**
	 * Color to rgb.
	 *
	 * @param alpha the alpha
	 * @param red the red
	 * @param green the green
	 * @param blue the blue
	 * @return the int
	 */
	private static int colorToRGB(int alpha, int red, int green, int blue) {
		int newPixel = 0;
		newPixel += alpha;
		newPixel = newPixel << 8;
		newPixel += red;
		newPixel = newPixel << 8;
		newPixel += green;
		newPixel = newPixel << 8;
		newPixel += blue;

		return newPixel;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return image.getWidth();
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return image.getHeight();
	}

	/**
	 * Save as file.
	 *
	 * @param filepath the filepath
	 * @param format the format
	 * @return the file
	 */
	public File saveAsFile(String filepath, String format) {

		File result = new File(filepath);
		try {
			ImageIO.write(image, format, result);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to write image");
		}
		return result;
	}

	/**
	 * Crops a fixed size piece of the image starting from the start point going up and right
	 *
	 * @param start the start
	 * @return the image
	 */

	public Image cropFromPoint(Point start) {
		int x = start.getXCoord();
		int y = start.getYCoord();
		BufferedImage cropped = image.getSubimage(x, 0, image.getWidth() - x,
				image.getHeight() - y);
		Image result = new Image(cropped);
		return result;
	}

	/**
	 * Returns the left sub image starting from the left corner 
	 * with the given width.
	 *
	 */
	public Image cropLeft(int width) {
		BufferedImage cropped = image
				.getSubimage(0, 0, width, this.getHeight());
		Image res = new Image(cropped);
		return res;
	}

	/**
	 * Crop area.
	 *
	 * @param start the crop starting point
	 * @param areaWidth the area width
	 * @param areaHeight the area height
	 */
	public Image cropArea(Point start, int areaWidth, int areaHeight) {
		int x = start.getXCoord();
		int y = start.getYCoord();

		BufferedImage cropped = image.getSubimage(
				Math.min(x, image.getWidth() - areaWidth), image.getHeight()
						- Math.max(areaHeight, y), areaWidth, areaHeight);
		Image result = new Image(cropped);
		return result;
	}

/**
 * Gets the pixel region average.
 * From the point it gets its neighbor pixels and finds the region average
 * @param point the point
 * @param backgroundColor is filtered from 
 * the average since eye sharpness differentiates it easily
 * @return the pixel region average color
 */
	public Color getPixelRegionAverage(Point point, Color backgroundColor) {
		if ((point.getXCoord() >= 1 && point.getXCoord() < image.getWidth())
				&& (point.getYCoord() >= 1 && point.getYCoord() < image
						.getHeight())) {
			Vector<Color> colors = new Vector<Color>();
			for (int i = point.getXCoord() - 1; i <= point.getXCoord() + 1; i++) {
				for (int j = point.getYCoord() - 1; j <= point.getYCoord() + 1; j++) {
					Color cur = getPixel(i, j).getColor();
					if (!cur.isEqualTo(backgroundColor)) {
						colors.add(cur);
					}
				}
			}
			return Color.getAverage(colors);
		}
		return getPixel(point).getColor();
	}
	
	/**
	 * Sets the color.
	 *
	 * @param x the x
	 * @param y the y
	 * @param color the color
	 */
	public void setColor(int x,int y, int color){
		image.setRGB(x, image.getHeight()-y, color);
	}
	
}
