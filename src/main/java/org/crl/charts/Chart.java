package org.crl.charts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.crl.exceptions.NoChartFoundException;
import org.crl.exceptions.NoDataException;
import org.crl.imagedata.Color;
import org.crl.imagedata.ColorUtilities;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.ChartConfigurator;
import org.crl.utilities.OCRReader;
import org.crl.utilities.Pair;

import net.sourceforge.tess4j.TesseractException;

public class Chart {
	private Image image;
	private ChartConfigurator commonConfig;
	private static final List<String> noDataMessages = Arrays.asList("No",
			"Data", "data", "available", "empty");

	public Chart(Image image, ChartConfigurator commonConfig) {
		setImage(image);
		if (!containsData()) {
			throw new IllegalStateException(
					"The image contains no identifiable chart data");

		}
		setCommonConfig(commonConfig);
	}

	public Chart(Image image) {
		setImage(image);
	}

	public ChartConfigurator getCommonConfig() {
		return commonConfig;
	}

	/**
	 * @param commonConfig
	 *            Configures the yAxis of a chart as double values and their
	 *            position
	 * */
	public void setCommonConfig(ChartConfigurator commonConfig) {
		this.commonConfig = commonConfig;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Verifies whether the chart image contains data through reading the date
	 * label in the center of the image
	 * */
	public boolean containsData() {
		// the no data label is at the center of the image
		Image cropped = image.cropArea(new Point(image.getWidth() / 2 - 50,
				image.getHeight() / 2 + 25), 100, 50);
		String result;
		// this scale level provides high recognition rate
		result = OCRReader.recognizeText(cropped.scale(7));

		// no data message was not read-it is likely that data is contained
		if (result == null) {
			return true;
		}

		for (int i = 0; i < noDataMessages.size(); i++) {
			if (result.contains(noDataMessages.get(i))) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Use this method when the no data message or its position is custom
	 * 
	 * @param messageStart
	 *            - the expected upper left corner of the message
	 * @param maxMessageBoxSize
	 *            - a pair with width and height of the maximal message size
	 * @param noDataMessage
	 *            - a custom no data message
	 * */
	public boolean containsData(Point messageStart,
			Pair<Integer, Integer> maxMessageBoxSize, String noDataMessage) {
		Image cropped = image.cropArea(messageStart,
				maxMessageBoxSize.getFirst(), maxMessageBoxSize.getSecond());
		String result;
		// this scale level provides high recognition rate
		result = OCRReader.recognizeText(cropped.scale(5));

		if (result == null) {
			return true;
		}
		String[] noDataCustom = noDataMessage.split(" ");
		for (int i = 0; i < noDataCustom.length; i++) {
			if (result.contains(noDataCustom[i])) {
				return false;
			}
		}
		/*
		 * Also tries with the standard message
		 */
		return containsData();
	}
}
