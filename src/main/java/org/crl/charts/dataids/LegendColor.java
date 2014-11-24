package org.crl.charts.dataids;

import org.crl.imagedata.Color;

/**
 * Color of legend in pie chart.
 */
public class LegendColor implements DataId {
	
	/** The legend color. */
	private Color legendColor;

	public Object getValue() {
		return legendColor;
	}

	public String toString() {
		return legendColor.toString();
	}

	public LegendColor(Color color) {
		this.legendColor = color;
	}

}
