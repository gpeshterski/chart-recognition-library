package org.crl.charts.linechart;

import org.crl.charts.XAxisTimeConfiguration;
import org.crl.charts.YAxisConfiguration;
import org.crl.imagedata.Color;
import org.crl.imagedata.Point;
import org.crl.utilities.ChartConfigurator;

/**
 * Includes the xAxis and yAxis configurations for a line chart
 * */
public class LineChartAxisConfig {
	/**
	 * Stores the time values at the axis and their position on the cropped line
	 * chart image
	 * */
	private XAxisTimeConfiguration xAxis;
	/**
	 * Configures the yAxis of a chart as double values and their position
	 * */
	private YAxisConfiguration yAxis;

	/**
	 * @param xAxis
	 *            Stores the time values at the axis and their position on the
	 *            cropped line chart image
	 * @param yAxis
	 *            Configures the yAxis of a chart as double values and their
	 *            position
	 * */
	public LineChartAxisConfig(XAxisTimeConfiguration xAxis,
			YAxisConfiguration yAxis) {
		this.xAxis = xAxis;
		this.yAxis = yAxis;

	}

	/**
	 * @return the scale value for a given point of the chart image
	 * */
	public double getScaleValue(Point point) {
		return yAxis.getValuePerStampLinear(point.getYCoord());
	}

	/**
	 * @return the time corresponding to a point as long value
	 * */
	public Long getTimeValue(Point point) {
		return xAxis.getTimePerTimestamp(point.getXCoord());
	}

}
