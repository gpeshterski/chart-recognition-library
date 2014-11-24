package org.crl.imagedata;

import java.util.Iterator;
import java.util.Map;

/**
 * Commonly used methods for an image 
 */
public class ColorUtilities {

	private Image image;
	
	/** The outer background color. */
	private Color outerBackgroundColor;

	public ColorUtilities(Image image) {
		this.image = image;
		outerBackgroundColor = findOuterBackgroundColor();
	}

	/**
	 * Verifies whether a color is dominant enough to be a dominant color.
	 *
	 * @param dominanceMap the dominance map
	 * @param minAcceptableDominance in pixel count
	 * @return true, if successful
	 */
	public boolean hasSufficientDominance(Map<Color, Integer> dominanceMap,
			int minAcceptableDominance) {
		if (getDominatingColor(dominanceMap, minAcceptableDominance) == null) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the dominating color  in a map.
	 *
	 * @param dominanceMap the dominance map
	 * @param minAcceptableDominance the min acceptable dominance
	 * @return the dominating color
	 */
	public Color getDominatingColor(Map<Color, Integer> dominanceMap,
			int minAcceptableDominance) {
		Iterator it = dominanceMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int repetitionCount = (Integer) pair.getValue();
			if (repetitionCount > minAcceptableDominance) {
				return (Color) pair.getKey();
			}
		}
		return null;

	}

	/**
	 * Find outer background color of the chart image.
	 * through trying to find an appropriate region on the left and bottom
	 *
	 * @return the color
	 */
	private Color findOuterBackgroundColor() {
		Color candidate = null;
		for (int regionBegX = 1; regionBegX < image.getWidth(); regionBegX++) {
			for (int regionBegY = 1; regionBegY < image.getHeight(); regionBegY++) {
				candidate = image.getPixel(regionBegX, regionBegY).getColor();
				if (isRegionHomegenous(regionBegX, regionBegY, 10)) {
					return candidate;
				}
			}
		}
		return candidate;
	}

	/**
	 * To identify the background we need a large enough piece with a single
	 * color.
	 * @param regionBegX the region beg x
	 * @param regionBegY the region beg y
	 * Represents the starting  point of the region
	 * 
	 * @param minRegionSize the minimal square region of a single color
	 * @return true, if the region contains a single color
	 */
	private boolean isRegionHomegenous(int regionBegX, int regionBegY,
			int minRegionSize) {
		Color leftNeighbor;
		Color current;
		Color lowerNeighbor;
		if (regionBegX + minRegionSize >= image.getWidth()
				|| regionBegY + minRegionSize >= image.getHeight()) {
			// we cannot settle the required region in the area
			return false;
		}
		for (int regionPointX = regionBegX + 1; regionPointX < regionBegX
				+ minRegionSize; regionPointX++) {
			for (int regionPointY = regionBegY + 1; regionPointY < regionBegY
					+ minRegionSize; regionPointY++) {
				lowerNeighbor = image.getPixel(regionPointX - 1, regionPointY)
						.getColor();
				leftNeighbor = image.getPixel(regionPointX, regionPointY - 1)
						.getColor();
				current = image.getPixel(regionPointX, regionPointY).getColor();
				if (!(current.isEqualTo(lowerNeighbor) && current
						.isEqualTo(leftNeighbor))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if region  is single color rectangle.
	 *
	 * @param regionBegX the region beg x
	 * @param regionBegY the region beg y
	 * @param minRegionSize the min region size
	 * @return true, if is region rectangle
	 */
	public boolean isRegionRectangle(int regionBegX, int regionBegY,
			int minRegionSize) {
		return isRegionHomegenous(regionBegX, regionBegY, minRegionSize);
	}

	/**
	 * A color is a candidate for dominance if it is not background.
	 *
	 * @param dominanceMap the dominance map
	 * @param point the point
	 */
	public void addToDominanceMap(Map<Color, Integer> dominanceMap, Point point) {
		Pixel pixel = image.getPixel(point.getXCoord(), point.getYCoord());

		if (!pixel.getColor().isEqualTo(outerBackgroundColor)) {
			int count;
			if (!dominanceMap.containsKey(pixel.getColor())) {
				dominanceMap.put(pixel.getColor(), 1);
			} else {
				count = dominanceMap.get(pixel.getColor());
				dominanceMap.put(pixel.getColor(), count + 1);
			}
		}
	}

	/**
	 * Any color is allowed to dominate in the given map.
	 *
	 * @param dominanceMap the dominance map
	 * @param point the point
	 */
	public void addToDominanceMapNonExclusive(Map<Color, Integer> dominanceMap,
			Point point) {
		Pixel pixel = image.getPixel(point.getXCoord(), point.getYCoord());
		int count;
		if (!dominanceMap.containsKey(pixel.getColor())) {
			dominanceMap.put(pixel.getColor(), 1);
		} else {
			count = dominanceMap.get(pixel.getColor());
			dominanceMap.put(pixel.getColor(), count + 1);
		}

	}

	/**
	 * Gets the outer background color.
	 *
	 * @return the outer background color
	 */
	public Color getOuterBackgroundColor() {
		return outerBackgroundColor;
	}

	/**
	 * Sets the outer background color.
	 *
	 * @param color the new outer background color
	 */
	public void setOuterBackgroundColor(Color color) {
		this.outerBackgroundColor = color;
	}
}
