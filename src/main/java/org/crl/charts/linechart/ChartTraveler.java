package org.crl.charts.linechart;

import org.crl.imagedata.Point;

/**
 *  ChartTraveller runs through the line of a chart and 
 *  extracts the pixels that are part of it.
 */
public class ChartTraveler {
	
	/** The current position of the traveler. */
	private Point currentPosition;
	
	/** The main line width of the chart */
	private int mainLineWidth;
	
	/** The image width. */
	private int imageWidth;
	
	/** The image height. */
	private int imageHeight;

	
	/**
	 *
	 * @param position the starting position
	 * @param mainLineWidth the main line width
	 * @param imageWidth the image width
	 * @param imageHeight the image height
	 */
	public ChartTraveler(Point position, int mainLineWidth, int imageWidth,
			int imageHeight) {
		this.currentPosition = position;
		this.mainLineWidth = mainLineWidth;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	/**
	 * Checks if the position moved to is still in the image
	 *
	 * @param position the position moved to
	 * @return true, if is valid
	 */
	private boolean isValid(Point position) {
		if ((position.getXCoord() >= mainLineWidth && position.getXCoord() < imageWidth)
				&& position.getYCoord() >= mainLineWidth
				&& position.getYCoord() < imageHeight) {
			return true;
		}
		return false;

	}

	/**
	 * Search for points that belongs to the line, upper from the current position
	 *
	 * @return null if the point is invalid
	 */
	public Point getHigher() {
		Point up = new Point(0, 1);
		Point resultPoint = currentPosition.move(up);
		if (isValid(resultPoint)){
			return resultPoint;
		}

		return null;
	}

	/**
	 * Search for points that belongs to the line, down from the current position
	 *
	 * @return null if the point is invalid
	 */
	public Point getLower() {
		Point down = new Point(0, -1);
		Point resultPoint = currentPosition.move(down);
		if (isValid(resultPoint)) {
			return resultPoint;
		}
		return null;
	}

	/**
	 * Search for points that belongs to the line, right from the current position
	 *
	 * @return null if the point is invalid
	 */
	public Point getForwarder() {
		Point forward = new Point(1, 0);
		Point resultPoint = currentPosition.move(forward);
		if (isValid(resultPoint)) {
			return resultPoint;
		}
		return null;

	}

	/**
	 * Gets the current position of the traveler.
	 *
	 * @return the position
	*/
	public Point getPosition() {
		return currentPosition;
	} 

}