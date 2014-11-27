package org.crl.charts;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.crl.charts.barchart.BarChart;
import org.crl.charts.barchart.BarChartAxisConfiguration;
import org.crl.charts.dataids.DataId;
import org.crl.charts.dataids.Label;
import org.crl.charts.linechart.LineChart;
import org.crl.charts.linechart.LineChartConfig;
import org.crl.imagedata.Image;
import org.crl.utilities.ChartConfigurator;
import org.crl.utilities.Pair;
import org.crl.utilities.ScaleRecognizer;
import org.crl.utilities.scaleparsing.DateParser;
import org.crl.utilities.scaleparsing.DateSequenceValidator;
import org.crl.utilities.scaleparsing.NumberSequenceValidator;
import org.crl.utilities.scaleparsing.fontutils.FontsDAL;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.junit.Test;

public class IstaTest {

	private FontsDAL font = new FontsDAL("DejaVMUni");

	@Test
	public void testLineChart() throws InterruptedException {
		Image image = new Image("src/test/resources/TestCharts/LineCharts/Uncut/cpu2.png");

		ChartCropper cropper = new ChartCropper(image);
		ChartConfigurator commonConfig = new ChartConfigurator(image);
		ScaleRecognizer recognizer = new ScaleRecognizer(image, cropper.getChartStart(), commonConfig);
		YAxisConfiguration yAxis = getYaxisAxisConfig(recognizer);
		// tries to identify as line chart
		HashMap<Long, Double> lineChartData = tryAsLineChart(recognizer, cropper, yAxis, commonConfig);

		System.out.println(lineChartData);
		
		//generate chart using jfreechart
		/*final TimeSeriesCollection dataset = new TimeSeriesCollection();
        final TimeSeries  series = new TimeSeries("data", Second.class);
        
		for (long date : lineChartData.keySet()) {
			series.addOrUpdate(new Second(new Date(date)), lineChartData.get(date));
		}
		dataset.addSeries(series);	

		final XYLineChartExample demo = new XYLineChartExample(dataset);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
        
        Thread.sleep(60000);*/

	}
	public void testBarChart() throws InterruptedException {
		Image image = new Image("src/test/resources/TestCharts/BarCharts/Uncut/bar01.png");
		
		ChartCropper cropper = new ChartCropper(image);
		ChartConfigurator commonConfig = new ChartConfigurator(image);
		ScaleRecognizer recognizer = new ScaleRecognizer(image, cropper.getChartStart(), commonConfig);
		YAxisConfiguration yAxis = getYaxisAxisConfig(recognizer);
		// tries to identify as line chart
		Vector<Pair<DataId, Double>> barChartData = tryAsBarChart(image, yAxis, commonConfig);
		System.out.println(barChartData);
		
		/*
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries  series = new TimeSeries("data", Second.class);
		
		for (long date : lineChartData.keySet()) {
			series.addOrUpdate(new Second(new Date(date)), lineChartData.get(date));
		}
		dataset.addSeries(series);	
		
		final XYLineChartExample demo = new XYLineChartExample(dataset);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		
		
		Thread.sleep(60000);
		*/
	}

	private YAxisConfiguration getYaxisAxisConfig(ScaleRecognizer recognizer) {
		Vector<Pair<Comparable, Integer>> scaleLabels = recognizer.getScaleLabels();
		NumberSequenceValidator scaleValidator = new NumberSequenceValidator(scaleLabels, font.getDigitErrors());
		YAxisConfiguration yAxis = new YAxisConfiguration(scaleValidator.getValidatedScale(scaleValidator
				.getValidSequence()));
		return yAxis;
	}

	private HashMap<Long, Double> tryAsLineChart(ScaleRecognizer recognizer, ChartCropper cropper,
			YAxisConfiguration yAxis, ChartConfigurator commonConfig) {

		Vector<Pair<String, Integer>> timeTextLabels = recognizer.getTimeLabelStrings();
		DateParser parser = new DateParser(timeTextLabels, font.getStringErrors());
		DateSequenceValidator dateValidator = new DateSequenceValidator(parser.getDates(), font.getDigitErrors(),
				parser.getDateFormat());
		Vector<Pair<Comparable, Integer>> scale = dateValidator.getValidatedScale(dateValidator.getValidSequence());

		for (Pair<Comparable, Integer> scaleElement : scale) {
			scaleElement.setSecond(scaleElement.getSecond() - cropper.getChartStart().getXCoord());
		}

		XAxisTimeConfiguration xAxis = new XAxisTimeConfiguration(scale);

		org.crl.charts.linechart.LineChartAxisConfig lineAxisConfig = new org.crl.charts.linechart.LineChartAxisConfig(
				xAxis, yAxis);

		LineChartConfig config = new LineChartConfig(lineAxisConfig);

		LineChart lineChart = new LineChart(cropper.cropAxises(), commonConfig, config);
		HashMap<Long, Double> lineChartData = (HashMap<Long, Double>) lineChart.extractTimeStampsValuePairs();
		return lineChartData;
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

}
