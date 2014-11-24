package org.crl.charts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crl.exceptions.ColorNotFoundException;
import org.crl.imagedata.Color;
import org.crl.imagedata.ColorUtilities;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;

/**
 * Crops an axis containing chart to only the image with its axis
 */
public class ChartCropper {

	private Image imageMono;
	
	/** The image. */
	private Image image;
	
	/** The outer background color. */
	private Color outerBackgroundColor;
	
	/** The utilities. */
	private ColorUtilities utilities;
	
	/** The min chart covered part.
	 * A chart should cover at least 40% of the image length and height 
	 * */
	private double minChartCoveredPart=0.4;
	
	private static final Log logger = LogFactory.getLog(ChartCropper.class);
	
	/** The chart start. */
	private Point chartStart;

	/**
	 * 
	 *
	 * @param image the image
	 */
	public ChartCropper(Image image) {
		this.image = image;
		this.imageMono=image.monochromize();
		utilities = new ColorUtilities(this.imageMono);
		outerBackgroundColor = utilities.getOuterBackgroundColor();
		if (outerBackgroundColor == null) {
			throw new ColorNotFoundException(
					ColorNotFoundException.BACKGROUND_NOT_FOUND);
		}
	}

	/**
	 * Find chart start as an intersection of y and x axis
	 *
	 * @return the point
	 */
	public Point findChartStart() {
		int failedCount = 0;
		for (int column = 0; column < imageMono.getWidth(); column++) {
			//tries to find a vertical line that is homogeneous enough to be a y axis
			if (isPotentialYAxis(column)) {
				for (int row = 0; row < imageMono.getHeight(); row++) {
					//tries to find a horizontal line that is homogeneous enough to be an x axis
					if (isPotentialXAxis(row)) {
						if (!imageMono.getPixel(column, row).getColor()
								.isEqualTo(outerBackgroundColor)) {
							return new Point(column, row);
						} else {
							//used to prevent cases of an image that was loading 
							failedCount++;
						}
						if (failedCount > 10) {
							throw new IllegalStateException(
									"Unable to find chart start.");
						}
					}
				}
			}
		}
		throw new IllegalStateException("Unable to find chart start.");
	}

	/**
	 * Gets the chart start.
	 *
	 * @return the chart start
	 */
	public Point getChartStart() {
		if (chartStart == null) {
			chartStart = findChartStart();
		}
		return chartStart;

	}

	/**
	 * Checks if is potential x axis.
	 *
	 * @param row the row
	 * @return true, if is potential x axis
	 */
	private boolean isPotentialXAxis(int row) {
		// the black color represents image color after monochromization
		boolean hasLongLine = false;
		int minLengthCount = (int) (imageMono.getWidth() * minChartCoveredPart);
		int lengthCount = 0;
		for (int xCoord = 1; xCoord < imageMono.getWidth(); xCoord++) {
			if (imageMono.getPixel(xCoord, row).getColor()
					.isEqualTo(StandardColors.BLACK)
					&& imageMono.getPixel(xCoord - 1, row).getColor()
							.isEqualTo(StandardColors.BLACK)) {
				lengthCount++;
				if (lengthCount > minLengthCount) {
					hasLongLine = true;
				}
			} else {
				lengthCount = 0;
			}
		}
		if (hasLongLine) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is potential y axis.
	 *
	 * @param column the column
	 * @return true, if is potential y axis
	 */
	private boolean isPotentialYAxis(int column) {
		boolean hasLong = false;
		int minLengthCount = (int) (imageMono.getHeight() * (minChartCoveredPart));
		int lengthCount = 0;
		for (int yCoord = 1; yCoord < imageMono.getHeight(); yCoord++) {
			if (imageMono.getPixel(column, yCoord).getColor()
					.isEqualTo(StandardColors.BLACK)
					&& imageMono.getPixel(column, yCoord - 1).getColor()
							.isEqualTo(StandardColors.BLACK)) {
				lengthCount++;
				if (lengthCount > minLengthCount) {
					hasLong = true;
				}
			} else {
				lengthCount = 0;
			}
		}
		if (hasLong) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the right limit.
	 *
	 * @return the right limit
	 */
	public int getRightLimit() {
		for (int column = image.getWidth() - 1; column > getChartStart()
				.getXCoord(); column--)
			if (isPotentialYAxis(column)) {
				return column;
			}
		return image.getWidth() - 1;
	}

	/**
	 * Crop axises to the main lines
	 *
	 * @return the cropped image
	 */
	public Image cropAxises() {
		Point chartStart = getChartStart();
		int rightLimit = getRightLimit();
		Image startCrop = image.cropFromPoint(chartStart);
		return startCrop.cropLeft(rightLimit - getChartStart().getXCoord());
	}
}
