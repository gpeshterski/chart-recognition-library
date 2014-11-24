package org.crl.charts.barchart;

import java.util.Vector;

import org.crl.charts.YAxisConfiguration;
import org.crl.imagedata.Point;
import org.crl.utilities.Pair;

/**
 * A Bar Chart has a Y axis values scale
 * 
 */
public class BarChartAxisConfiguration {
	/**Configures the yAxis of a chart as double values and their position
	 * */
	private YAxisConfiguration yAxis;
	
	public BarChartAxisConfiguration(YAxisConfiguration yAxis) {
		this.yAxis = yAxis;
	}

	/**
	 * Given the value of the yCoord of a point in pixels, calculates the
	 * corresponding value of the scale
	 * */
	public double getScaleValue(int yCoord) {
		return yAxis.getValuePerStampLinear(yCoord);
	}
}
