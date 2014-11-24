package org.crl.charts.linechart;

import org.crl.charts.linechart.LineChartAxisConfig;
import org.crl.imagedata.Color;

/**
 * Includes the properties of a line chart that might be set manually or
 * identified by an algorithm automatically
 * */
public class LineChartConfig {

	/** The axis config. */
	private LineChartAxisConfig axisConfig;

	/** The chart line color. */
	private Color chartLineColor;

	/** The color that points the lowest and highest point value in the chart */
	private Color extremalsColor;

	/**
	 * Creates an empty line chart configuration (appropriate for fully
	 * automatic mode)
	 * */
	public LineChartConfig() {

	}

	/**
	 * Instantiates a new line chart config.
	 * 
	 * @param axisConfig
	 *            the axis config
	 */
	public LineChartConfig(LineChartAxisConfig axisConfig) {
		this.axisConfig = axisConfig;
	}

	/**
	 * Gets the axis config.
	 * 
	 * @return the axis config
	 */
	public LineChartAxisConfig getAxisConfig() {
		return axisConfig;
	}

	/**
	 * Sets the axis configuration.
	 * 
	 * @param axisConfig
	 *            the new axis configuration
	 */
	public void setAxisConfiguration(LineChartAxisConfig axisConfig) {
		this.axisConfig = axisConfig;
	}

	/**
	 * Gets the chart line color.
	 * 
	 * @return the chart line color
	 */
	public Color getChartLineColor() {
		return chartLineColor;
	}

	/**
	 * Sets the chart line color.
	 * 
	 * @param chartLineColor
	 *            the new chart line color
	 */
	public void setChartLineColor(Color chartLineColor) {
		this.chartLineColor = chartLineColor;
	}

	/**
	 * Gets the extremals color.
	 * 
	 * @return the extremals color
	 */
	public Color getExtremalsColor() {
		return extremalsColor;
	}

	/**
	 * Sets the extremals color.
	 * 
	 * 
	 * @param extremalsColor
	 *            the new extremals color
	 */
	public void setExtremalsColor(Color extremalsColor) {
		this.extremalsColor = extremalsColor;
	}

}
