package org.crl.utilities;

import org.crl.imagedata.Color;
import org.crl.imagedata.ColorUtilities;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;

/**
 * Universal chart configuration properties for axised charts
 * including background color and main axis line width
 */
public class ChartConfigurator {

	/** The background color. */
	private Color backgroundColor;
	
	/** The main line width. */
	private int mainLineWidth;
	
	/** The image. to be analyzed */
	private Image image;
	
	/** Useful uilities in working with a specific chart. */
	private ColorUtilities utilities;

	/**
	 *
	 * @param backgroundColor the background color of the image
	 * @param mainLineWidth the main line width
	 */
	public ChartConfigurator(Color backgroundColor, int mainLineWidth) {
		this.backgroundColor=backgroundColor;
	    this.mainLineWidth=mainLineWidth;
	}

	/**
	 * Receives a cropped image.
	 */
	public ChartConfigurator(Image image) {
		this.backgroundColor = StandardColors.WHITE;
		this.mainLineWidth = 1;
		this.image = image;
	}

	public ChartConfigurator() {
	
		utilities = new ColorUtilities(image);
		backgroundColor = findBackgroundColor();
		mainLineWidth = findMainLineWidth();
	}
/**
 * 
 * */
	public Color findBackgroundColor() {
		Color result = null;
		for (int i = 1; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if (utilities.isRegionRectangle(i, j, 10)) {
					return image.getPixel(i, j).getColor();
				}
			}
		}
		// in case of failure white is most likely
		return StandardColors.WHITE;
	}

	/**
	 * Find main line width - the line of crossing axis.
	 *
	 */
	public int findMainLineWidth() {
		int width = 1;
		// the exact crossing of the lines might result in slight color
		// difference
		Color lineColor = image.getPixel(3, 0).getColor();
		for (int j = 1; j < image.getHeight(); j++) {
			if (!image.getPixel(3, j).getColor().isComparableTo(lineColor)) {
				break;
			}
			width++;

		}
		return width;
	}

	public Color getBackgroundColor() {

		return backgroundColor;
	}

	public int getMainLineWidth() {

		return mainLineWidth;
	}

	public void setMainLineWidth(int mainLineWidth) {
		this.mainLineWidth = mainLineWidth;
	}
}
