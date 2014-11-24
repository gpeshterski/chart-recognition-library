package org.crl.charts;

import java.util.Vector;

import org.crl.imagedata.Point;
import org.crl.utilities.Pair;

/**
 * Configures the yAxis of a chart as double values and their position
 */
public class YAxisConfiguration {
	
	/** The scale labels. */
	private Vector<Pair<Comparable, Integer>> scaleLabels;
	
	/**
	Use this to set yAxis as at least two(for linear scale) or more
	Pairs of Double value and their pixel position on the chart
	 */
	public YAxisConfiguration(Vector<Pair<Comparable, Integer>> scaleLabels) {
		this.scaleLabels = scaleLabels;
	}
	
	/**
	 * Gets the value per linear stamp.
	 *
	 * @param position the position at the yAxis in pixels from the bootom of the image
	 * @return the value per stamp linear
	 */
	public double getValuePerStampLinear(int position) {
		Integer scaleLow = scaleLabels.firstElement().getSecond();
		Double scaleLowValue = (Double) scaleLabels.firstElement().getFirst();
		double result = getValuePerPixelLinear() * (position - scaleLow);
		return result + scaleLowValue;
	}

	/**
	 * Gets the corresponding value per 1 pixel in a linear scale.
	 *
	 * @return the value per pixel linear
	 */
	private double getValuePerPixelLinear() {
		double result;
		Integer scaleHigh = scaleLabels.lastElement().getSecond();
		Integer scaleLow = scaleLabels.firstElement().getSecond();
		Double scaleHighValue = (Double) scaleLabels.lastElement().getFirst();
		Double scaleLowValue = (Double) scaleLabels.firstElement().getFirst();
		result = (scaleHighValue - scaleLowValue) / (scaleHigh - scaleLow);
		return result;
	}
}
