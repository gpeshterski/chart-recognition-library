package org.crl.charts.barchart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.crl.charts.ChartCropper;
import org.crl.charts.YAxisConfiguration;
import org.crl.imagedata.Image;
import org.crl.utilities.ChartConfigurator;
import org.crl.utilities.Pair;
import org.crl.utilities.ScaleRecognizer;

/**
 * Analyzes a bar chart and extracts the data for its bars
 */
public class BarChart {
	private Vector<Double> barChartHeights;
	private Image rawImage;
	private BarChartAxisConfiguration chartConfig;
	private ChartConfigurator commonConfig;

	/**
	 * 
	 * @param rawImage
	 *            a non cropped image to be analyzed
	 */
	public BarChart(Image rawImage, BarChartAxisConfiguration chartConfig,
			ChartConfigurator commonConfig) {
		this.rawImage = rawImage;
		this.chartConfig = chartConfig;
		this.commonConfig = commonConfig;
	}

	/**
	 * Tries different strategies for the two different bar chart types
	 * <li>
	 * The bars have scrapers at the bottom they will be analyzed through
	 * searching for data around those scrapers
	 * </li>
	 * 
	 * <li>
	 * The bar chart does not have bar scrapers at the bottom bars are
	 * identified through finding appropriate rectangles at the bottom
	 * </li>
	 * @return the values extracted for the bars
	 */
	public Vector<Double> extractChart() {

		ChartCropper cropper = new ChartCropper(rawImage);
		ScaleRecognizer recognizer = new ScaleRecognizer(rawImage,
				cropper.getChartStart(), commonConfig);
		Vector<Double> data;
		Vector<Integer> barCenters = new Vector<Integer>();
		// tries alternative one
		/*
		 * The bars have scrapers at the bottom they will be analyzed through
		 * searching for data around those scrapers
		 */
		try {

			Map<Integer, Integer> barScrapers = recognizer
					.getLongestXScaleScrapers();
			Iterator it = barScrapers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				barCenters.add((Integer) pair.getKey());
			}
			// way too many bars found
			// violates the general bar chart representation
			if (barCenters.size() > rawImage.getWidth() / 20) {
				throw new IllegalStateException("The barchart is not scrapered");
			}
		} catch (Exception ex) {
			// in case of fail
			// tries alternative 2
			/*
			 * The bar chart does not have bar scrapers at the bottom bars are
			 * identified through finding appropriate rectangles at the bottom
			 */

			BarChartDistanced distanced = new BarChartDistanced(
					cropper.cropAxises(), commonConfig, chartConfig);
			barCenters = distanced.findBarsCenters();
		}
		// known the positions of the bars extracts their data
		BarChartExtractor extractor = new BarChartExtractor(
				cropper.cropAxises(), chartConfig, commonConfig, barCenters);
		data = extractor.getAllBarsHeights();

		return data;
	}
}
