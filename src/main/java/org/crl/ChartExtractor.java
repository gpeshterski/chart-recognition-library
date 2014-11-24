package org.crl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.crl.charts.Chart;
import org.crl.charts.ChartCropper;
import org.crl.charts.XAxisTimeConfiguration;
import org.crl.charts.YAxisConfiguration;
import org.crl.charts.barchart.BarChart;
import org.crl.charts.barchart.BarChartAxisConfiguration;
import org.crl.charts.barchart.BarChartConfig;
import org.crl.charts.dataids.DataId;
import org.crl.charts.dataids.Label;
import org.crl.charts.dataids.LegendColor;
import org.crl.charts.dataids.Timestamp;
import org.crl.charts.linechart.LineChart;
import org.crl.charts.linechart.LineChartAxisConfig;
import org.crl.charts.linechart.LineChartConfig;
import org.crl.charts.piechart.PieChart;
import org.crl.charts.piechart.PieChartConfig;
import org.crl.exceptions.DataExtractionException;
import org.crl.exceptions.NoDataException;
import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.ChartConfigurator;
import org.crl.utilities.MapUtilities;
import org.crl.utilities.Pair;
import org.crl.utilities.ScaleRecognizer;
import org.crl.utilities.scaleparsing.DateParser;
import org.crl.utilities.scaleparsing.DateSequenceValidator;
import org.crl.utilities.scaleparsing.NumberSequenceValidator;
import org.crl.utilities.scaleparsing.SequenceValidator;
import org.crl.utilities.scaleparsing.fontutils.FontsDAL;

/**
 * Extracts data given an image in automatic mode Or using a configuration to
 * specify options for the desired chart type
 */
public class ChartExtractor {

	/** The Default font is the extensively used in VCOps DejaVMUni. */
	public static final String DEFAULT_FONT = "DejaVMUni";

	/** The font used in the charts case insensitive */
	private FontsDAL font;

	/**
	 * 
	 * @param fontName
	 *            the name of the font used in the charts case insensitive
	 */
	public ChartExtractor(String fontName) {
		font = new FontsDAL(fontName);
	}

	/**
	 * Identifies the chart type and extracts its data.
	 * <p>
	 * States of an image recognized by automatic mode
	 * <li>Line Chart</li>
	 * <li>Bar Chart</li>
	 * <li>Pie Chart</li>
	 * <li>No data</li>
	 * <li>Image not recognized</li>
	 * </p>
	 * 
	 * @param image
	 *            the image to be analyzed
	 * @return the extracted chart data
	 */
	public Vector<Pair<DataId, Double>> getChartData(Image image) {
		try {
			Chart chart = new Chart(image);
			boolean containsData = chart.containsData();
			// verifies if data is contained
			if (!containsData) {
				throw new NoDataException();
			}
			try {
				// tries to identify as pie chart
				return tryAsPieChart(image);
			} catch (DataExtractionException pieChartException) {

				ChartCropper cropper;
				ScaleRecognizer recognizer;
				ChartConfigurator commonConfig = null;
				YAxisConfiguration yAxis = null;
				try {
					// common part for bar and line chart to prevent performance issues due to dupliction
					cropper = new ChartCropper(image);
					commonConfig = new ChartConfigurator(image);
					recognizer = new ScaleRecognizer(image,
							cropper.getChartStart(), commonConfig);
					yAxis = getYaxisAxisConfig(recognizer);
					// tries to identify as line chart
					HashMap<Long, Double> lineChartData = tryAsLineChart(
							recognizer, cropper, yAxis, commonConfig);
					return convertToUnifiedFormat(lineChartData);
				} catch (Exception lineChartException) {
					try {
						// tries to identify as bar chart
						return tryAsBarChart(image, yAxis, commonConfig);
					} catch (Exception noChart) {
						throw new IllegalStateException("No chart identified");

					}
				}
			}
		} catch (NoDataException noData) {
			throw new IllegalStateException(noData);

		} catch (Exception generic) {
			throw new IllegalStateException(
					new DataExtractionException(generic));
		}
	}

	/**
	 * Extracts the data from a pie chart image, supporting further
	 * configuration.
	 * 
	 * @param image
	 *            the image to be analyzed
	 * @param config
	 *            the chart configuration
	 * @return the extracted chart data
	 */
	public Vector<Pair<LegendColor, Double>> getPieChartData(Image image,
			PieChartConfig config) {
		try {
			Chart chart = new Chart(image);
			boolean containsData = chart.containsData();
			if (!containsData) {
				throw new NoDataException();
			}
			PieChart pieChart = new PieChart(image);
			Vector<Pair<LegendColor, Double>> pieChartData = new Vector<Pair<LegendColor, Double>>();
			Color specificColor = config.getChartColor();
			// in case of a color return data for that one only
			if (specificColor != null) {
				pieChartData.add(new Pair<LegendColor, Double>(new LegendColor(
						specificColor), pieChart
						.getColorPercentage(specificColor)));
			} else {
				// extracts data for all colors
				Vector<Pair<Color, Double>> pieChartDataResult = pieChart
						.getPercentagePerColors();
				for (Pair<Color, Double> pair : pieChartDataResult) {
					pieChartData
							.add(new Pair<LegendColor, Double>(new LegendColor(
									pair.getFirst()), pair.getSecond()));
				}

			}
			return pieChartData;
		} catch (NoDataException noData) {

			throw new IllegalStateException(noData);
		} catch (Exception commonException) {
			throw new DataExtractionException("message", commonException);
		}
	}

	/**
	 * Extracts the data from a line chart image, supporting further
	 * configuration.
	 * 
	 * @param config
	 *            the line chart specific configuration
	 * @return the extracted chart data
	 */
	public Vector<Pair<Long, Double>> getLineChartData(Image image,
			LineChartConfig config) {
		try {
			// if any configuration is added use it, otherwise try to find the
			// details
			ChartCropper cropper = new ChartCropper(image);
			ChartConfigurator commonConfig = new ChartConfigurator(image);
			ScaleRecognizer recognizer = new ScaleRecognizer(image,
					cropper.getChartStart(), commonConfig);

			Vector<Pair<String, Integer>> timeTextLabels = recognizer.getTimeLabelStrings();
			if(timeTextLabels.size()==0){
				throw new IllegalStateException("Unable to recognize time labels.");
			}

			DateParser parser = new DateParser(timeTextLabels,
					font.getStringErrors());
			DateSequenceValidator dateValidator = new DateSequenceValidator(
					parser.getDates(), font.getDigitErrors(),
					parser.getDateFormat());
			Vector<Pair<Comparable, Integer>> scale = dateValidator
					.getValidatedScale(dateValidator.getValidSequence());
			for (int i = 0; i < scale.size(); i++) {
				scale.get(i).setSecond(
						scale.get(i).getSecond()
								- cropper.getChartStart().getXCoord());
			}
			XAxisTimeConfiguration xAxis = new XAxisTimeConfiguration(scale);

			Vector<Pair<Comparable, Integer>> scaleLabels = recognizer
					.getScaleLabels();
			NumberSequenceValidator scaleValidator = new NumberSequenceValidator(
					scaleLabels, font.getDigitErrors());
			YAxisConfiguration yAxis = new YAxisConfiguration(
					scaleValidator.getValidatedScale(scaleValidator
							.getValidSequence()));
			LineChartAxisConfig axisConfig = new LineChartAxisConfig(xAxis,
					yAxis);
			if (config.getAxisConfig() == null) {
				config.setAxisConfiguration(axisConfig);
			}
			LineChart chart = new LineChart(cropper.cropAxises(), commonConfig,
					config);
			HashMap<Long, Double> lineChartData = (HashMap<Long, Double>) chart
					.extractTimeStampsValuePairs();
			return sortByTimestamp(MapUtilities.toVector(lineChartData));
		} catch (Exception ex) {
			throw new IllegalStateException(new DataExtractionException(ex));
		}
	}

	/**
	 * Sort by time stamp ascending.
	 * 
	 * @param values
	 *            the values to sort
	 * @return the sorted vector
	 */
	private static Vector<Pair<Long, Double>> sortByTimestamp(
			Vector<Pair<Long, Double>> values) {
		Collections.sort(values, new Comparator<Pair<Long, Double>>() {
			public int compare(Pair<Long, Double> o1, Pair<Long, Double> o2) {
				if (o1.getFirst().equals(o2.getFirst())) {
					return o1.getSecond().compareTo(o2.getSecond());
				}
				return o1.getFirst().compareTo(o2.getFirst());
			}
		});
		return values;
	}

	/**
	 * Extracts the data from a bar chart image, supporting further
	 * configuration.
	 */
	public Vector<Pair<String, Double>> getBarChartData(Image image,
			BarChartConfig config) {
		try {
			Chart chart = new Chart(image);
			boolean containsData = chart.containsData();
			if (!containsData) {

				throw new NoDataException();
			}
			Vector<Pair<String, Double>> result = new Vector<Pair<String, Double>>();
			ChartConfigurator commonConfig = new ChartConfigurator(image);
			ChartCropper cropper = new ChartCropper(image);
			ScaleRecognizer recognizer = new ScaleRecognizer(image,
					cropper.getChartStart(), commonConfig);
			Vector<Pair<Comparable, Integer>> scaleLabels = recognizer
					.getScaleLabels();
			NumberSequenceValidator scaleValidator = new NumberSequenceValidator(
					scaleLabels, font.getDigitErrors());
			YAxisConfiguration yAxis = new YAxisConfiguration(
					scaleValidator.getValidatedScale(scaleValidator
							.getValidSequence()));
			BarChartAxisConfiguration chartConfig = new BarChartAxisConfiguration(
					yAxis);
			BarChart barChart = new BarChart(image, chartConfig, commonConfig);
			Vector<Double> barChartData = barChart.extractChart();

			Vector<Pair<String, Double>> barChartDataResult = new Vector<Pair<String, Double>>();
			for (int j = 0; j < barChartData.size(); j++) {
				if (barChartData.get(j) == Double.MAX_VALUE) {
					throw new IllegalStateException(
							"The values are too high, probably a wrong image type was provided");
				}
				barChartDataResult.add(new Pair<String, Double>("Bar " + j,
						barChartData.get(j)));
			}
			return barChartDataResult;
		} catch (NoDataException noData) {
			throw new IllegalStateException(noData);
		} catch (Exception genericException) {
			throw new IllegalStateException(new DataExtractionException(
					genericException));
		}
	}

	private Vector<Pair<DataId, Double>> tryAsPieChart(Image image) {
		PieChart pieChart = new PieChart(image);
		Vector<Pair<Color, Double>> pieChartData = pieChart
				.getPercentagePerColors();
		if (pieChartData.isEmpty()) {
			throw new DataExtractionException(
					"Failed to recognize as a pie chart");
		}
		Vector<Pair<DataId, Double>> pieResult = new Vector<Pair<DataId, Double>>();
		for (Pair<Color, Double> pair : pieChartData) {
			pieResult.add(new Pair<DataId, Double>(new LegendColor(pair
					.getFirst()), pair.getSecond()));
		}
		return pieResult;
	}

	private Vector<Pair<DataId, Double>> tryAsBarChart(Image image,
			YAxisConfiguration yAxis, ChartConfigurator commonConfig) {

		BarChartAxisConfiguration chartConfig = new BarChartAxisConfiguration(
				yAxis);

		BarChart barChart = new BarChart(image, chartConfig, commonConfig);
		Vector<Double> barChartData = barChart.extractChart();
		Vector<Pair<DataId, Double>> barChartDataResult = new Vector<Pair<DataId, Double>>();
		for (int j = 0; j < barChartData.size(); j++) {
			if (barChartData.get(j) > Double.MAX_VALUE) {
				throw new IllegalStateException(
						"The values are too high, probably a wrong image type was provided");
			}
			barChartDataResult.add(new Pair<DataId, Double>(new Label("Bar "
					+ (j + 1)), barChartData.get(j)));
		}
		return barChartDataResult;
	}

	private Vector<Pair<DataId, Double>> convertToUnifiedFormat(
			HashMap<Long, Double> lineChartData) {
		Vector<Pair<Long, Double>> barData = MapUtilities
				.toVector(lineChartData);
		Vector<Pair<DataId, Double>> dataConverted = new Vector<Pair<DataId, Double>>();
		for (Pair<Long, Double> pair : barData) {
			dataConverted.add(new Pair<DataId, Double>(new Timestamp(pair
					.getFirst()), pair.getSecond()));
		}
		return dataConverted;
	}

	private HashMap<Long, Double> tryAsLineChart(ScaleRecognizer recognizer,
			ChartCropper cropper, YAxisConfiguration yAxis,
			ChartConfigurator commonConfig) {

		Vector<Pair<String, Integer>> timeTextLabels = recognizer
				.getTimeLabelStrings();
		DateParser parser = new DateParser(timeTextLabels,
				font.getStringErrors());
		DateSequenceValidator dateValidator = new DateSequenceValidator(
				parser.getDates(), font.getDigitErrors(),
				parser.getDateFormat());
		Vector<Pair<Comparable, Integer>> scale = dateValidator
				.getValidatedScale(dateValidator.getValidSequence());

		for (Pair<Comparable, Integer> scaleElement : scale) {
			scaleElement.setSecond(scaleElement.getSecond()
					- cropper.getChartStart().getXCoord());
		}

		XAxisTimeConfiguration xAxis = new XAxisTimeConfiguration(scale);

		org.crl.charts.linechart.LineChartAxisConfig lineAxisConfig = new org.crl.charts.linechart.LineChartAxisConfig(
				xAxis, yAxis);

		LineChartConfig config = new LineChartConfig(lineAxisConfig);

		LineChart lineChart = new LineChart(cropper.cropAxises(), commonConfig,
				config);
		HashMap<Long, Double> lineChartData = (HashMap<Long, Double>) lineChart
				.extractTimeStampsValuePairs();
		return lineChartData;
	}

	private YAxisConfiguration getYaxisAxisConfig(ScaleRecognizer recognizer) {
		Vector<Pair<Comparable, Integer>> scaleLabels = recognizer
				.getScaleLabels();
		NumberSequenceValidator scaleValidator = new NumberSequenceValidator(
				scaleLabels, font.getDigitErrors());
		YAxisConfiguration yAxis = new YAxisConfiguration(
				scaleValidator.getValidatedScale(scaleValidator
						.getValidSequence()));
		return yAxis;
	}
}
