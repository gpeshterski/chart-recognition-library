package org.crl.charts.piechart;

import java.util.HashMap;
import java.util.Vector;

import org.crl.charts.Chart;
import org.crl.exceptions.ColorNotFoundException;
import org.crl.imagedata.Color;
import org.crl.imagedata.ColorUtilities;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.Pair;

import org.crl.imagedata.*;

/**
 * Extracts data for 2D PieChart.
 */
public class PieChart extends Chart {
	
	/** The legend height calculated */
	private int legendHeight;

	private ColorUtilities utilities;

	public PieChart(Image image) {
		super(image);
		//the default starting point for legend search
		legendHeight = getImage().getHeight() - 20;
		utilities = new ColorUtilities(image);
	}

	/**
	 * Gets the percentage for specific color.
	 *
	 * @param color the pie color to analyze
	 * @return the percentage corresponding to the color
	 */
	public double getColorPercentage(Color color) {
		Vector<Pair<Color, Double>> pc = getPercentagePerColors();
		for (int i = 0; i < pc.size(); i++) {
			if (pc.get(i).getFirst().isComparableTo(color)) {
				return pc.get(i).getSecond();
			}
		}
		throw new ColorNotFoundException(
				"The expected color was not found in the chart.");
	}

	/**
	 * Find pie image colors.
	 *
	 *@return the map of colors and their corresponding 
	 */
	private HashMap<Color, Integer> findPieImageColors() {
		HashMap<Color, Integer> dominanceMap = new HashMap<Color, Integer>();

		for (int x = 1; x < getImage().getWidth(); x++) {
			for (int y = legendHeight; y < getImage().getHeight(); y++) {
				utilities.addToDominanceMap(dominanceMap, new Point(x, y));
			}
		}
		return dominanceMap;
	}

	/**
	 * Find legend colors through identifying legend color squares.
	 *
	 */
	public Vector<Color> findLegendColors() {
		Vector<Color> legendColors = new Vector<Color>();
		/* The legend color surrounding box color. 
		 */
	    Color legendColorSurroundingBoxColor = StandardColors.BLACK;
		
	    /*
	     * Natural limits to a legend box
	     * */
		int maxLegendWidth = 40;
		int minLegendWidth = 8;
		
		int legendBoxHeight = 30;
		boolean lineFlag;
		
		for (int row = 1; row < getImage().getHeight(); row++) {
			for (int column = 1; column < getImage().getWidth(); column++) {
				Color current = getImage().getPixel(column, row).getColor();
				if ((!current.isComparableTo(utilities
						.getOuterBackgroundColor()))
						&& (!current
								.isComparableTo(legendColorSurroundingBoxColor))) {
					int width = 1;
					/*
					 * System.out .println(current + "   X " + column + " Y " +
					 * row);
					 */
					lineFlag = false;
					while (true) {
						// the image has ended
						if (column + width > getImage().getWidth()) {
							break;
						}
						Color next = getImage().getPixel(column + width, row)
								.getColor();
						// the area has ended
						if (!current.isEqualTo(next)) {

							// the area is a legend piece
							if (width >= minLegendWidth) {
								// the legend piece should be a rectangle
								if (utilities.isRegionRectangle(column + 1,
										row + 1, minLegendWidth)) {
									legendColors.add(current);
								}
							}
							break;
						}

						width++;
						if (width > maxLegendWidth) {
							// a legend box is not that large
							lineFlag = true;
							break;
						}

					}
					if (lineFlag == true) {
						// this is a line we skip it
						column++;
						break;
					}
					// we continue after the zone
					column += (width - 1);
				}

			}
			if (legendColors.size() >= 1) {
				legendHeight = row + legendBoxHeight;
				break;
			}
		}
		return legendColors;
	}

	/**
	 * Based on the found legend colors this method returns how many percent
	 * of the found legend colors is found.
     * Due to boundary color there might be a low standard deviation
     * 
	 * @return the percentage per colors
	 */
	public Vector<Pair<Color, Double>> getPercentagePerColors() {
		Vector<Color> legendColors = findLegendColors();
		HashMap<Color, Integer> dominanceMap = findPieImageColors();
		Vector<Pair<Color, Double>> countPerContainedColor = new Vector<Pair<Color, Double>>();
		Vector<Color> notContainedInPie = new Vector<Color>();
		int pieArea = 0;
		for (int i = 0; i < legendColors.size(); i++) {
			Color current = legendColors.elementAt(i);
			if (dominanceMap.containsKey(current)) {
				int count = dominanceMap.get(current);
				Pair<Color, Double> e = new Pair<Color, Double>(current,
						(double) count);
				countPerContainedColor.add(e);
				pieArea += count;
				dominanceMap.remove(current);
			} else {
				notContainedInPie.add(current);
			}
		}

		for (int i = 0; i < countPerContainedColor.size(); i++) {
			double percentage = (countPerContainedColor.elementAt(i)
					.getSecond() * 100) / pieArea;
			countPerContainedColor.elementAt(i).setSecond(percentage);
		}

		return countPerContainedColor;
	}

}
