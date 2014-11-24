package org.crl.charts.barchart;

import org.crl.imagedata.Color;

/**
 * Configurates a bar chart with options
 */
public class BarChartConfig {

	/** The bar color to be analyzed. */
	private Color barColor;

	/**
	 * The axis config for a bar chart is Y axis automatic configuration
	 * */
	private BarChartAxisConfiguration axisConfig;

	/**
	 * Creates an empty bar chart configuration
	 * */
	public BarChartConfig() {
	}

	/**
	 * 
	 * @param axisConfig
	 *            the axis config to be used
	 */
	public BarChartConfig(BarChartAxisConfiguration axisConfig) {
		this.axisConfig = axisConfig;
	}

	/** The bar color to be analyzed. */
	public Color getBarColor() {
		return barColor;
	}

	/** The bar color to be analyzed. */
	public void setBarColor(Color barColor) {
		this.barColor = barColor;
	}

	public BarChartAxisConfiguration getAxisConfig() {
		return axisConfig;
	}

	/** A Bar Chart has a Y axis values scale */
	public void setAxisConfig(BarChartAxisConfiguration axisConfig) {
		this.axisConfig = axisConfig;
	}

}
