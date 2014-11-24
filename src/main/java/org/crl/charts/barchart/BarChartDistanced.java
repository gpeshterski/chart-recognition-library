package org.crl.charts.barchart;

import java.util.Vector;

import org.crl.charts.Chart;
import org.crl.exceptions.ColorNotFoundException;
import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.ChartConfigurator;

import org.crl.imagedata.*;

/**
 * Expects a chart that starts from a coordinate system
 */
public class BarChartDistanced extends Chart {
	/**
	 * It is expected that all bars in an analysis run share the same solid bar color
	 */
	private Color barColor;
	/**
	 * The first bar stars at a specific distance from the beginning of the
	 * coordinate system
	 */
	private Integer lengthToFirstBar;
	/**
	 * The count of bars that were discovered
	 */
	private Integer barsCount;

	/**
	 * For the interval of blur surrounding the bars in pixels
	 */
	private static final Integer MIN_BAR_WIDTH = 5;

	/**
	 * The default background color. Will be added to the standard colors
	 */
	private BarChartAxisConfiguration scaleConfig;

	public Integer findLengthToFirstBar() {
		int lengthResult = 0;
		int pointXCoord = getCommonConfig().getMainLineWidth();
		int pointYCoord = getCommonConfig().getMainLineWidth();

		while (getImage().getPixel(pointXCoord, pointYCoord).getColor()
				.isEqualTo(StandardColors.MONO_WHITE)) {
			pointXCoord++;
		}
		lengthResult = pointXCoord;
		return lengthResult;
	}

	public BarChartDistanced(Image image, ChartConfigurator chartConfig,
			BarChartAxisConfiguration scaleConfig) {
		super(image, chartConfig);
		this.scaleConfig = scaleConfig;
		lengthToFirstBar = findLengthToFirstBar();
		barColor = findBarsColor();
		barsCount = 0;
	}

	/**
	 * Finds the first likely bar color
	 * 
	 * @return the color found
	 */
	public Color findBarsColor() {
		Color barColorResult = null;
		int j = getCommonConfig().getMainLineWidth();
		for (int i = lengthToFirstBar; i < getImage().getWidth(); i++) {
			if (!getImage().getPixel(i, j).getColor()
					.isEqualTo(StandardColors.MONO_WHITE)) {
				int adjacentEqual = 0;
				while (getImage()
						.getPixel(i, j)
						.getColor()
						.isEqualTo(getImage().getPixel((i) + 1, j).getColor())) {
					i++;
					adjacentEqual++;
					if (i == getImage().getWidth() - 1)
						break;
				}
				if (adjacentEqual > MIN_BAR_WIDTH) {
					return getImage().getPixel(i - 1, j).getColor();
				}
			}
		}
		if (barColorResult == null)
			try {
				throw new ColorNotFoundException(
						ColorNotFoundException.NO_BAR_COLOR_FOUND);
			} catch (ColorNotFoundException e) {
				e.printStackTrace();
			}
		;
		return null;
	}

	/**
	 * 
	 * @return Vector with the positons of the bars in the x axis
	 */
	public Vector<Integer> findBarsCenters() {
		Vector<Integer> barCenters = new Vector<Integer>();
		int j = getCommonConfig().getMainLineWidth();
		for (int i = lengthToFirstBar; i < getImage().getWidth(); i++) {

			if (!getImage().getPixel(i, j).getColor()
					.isEqualTo(StandardColors.MONO_WHITE)) {
				int adjacentEqual = 0;
				while (getImage()
						.getPixel(i, j)
						.getColor()
						.isEqualTo(getImage().getPixel((i) + 1, j).getColor())) {
					i++;
					adjacentEqual++;
					if (i == getImage().getWidth() - 1) {
						break;
					}
				}
				if (adjacentEqual > MIN_BAR_WIDTH) {
					barCenters.add(i - adjacentEqual / 2);
					barsCount++;
				}
			}
		}
		return barCenters;
	}

}
