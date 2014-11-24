package org.crl.utilities;

import static org.junit.Assert.*;

import org.crl.charts.ChartCropper;
import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;
import org.junit.Test;

public class ChartCropperTest {
/*
	private ChartCropper loadCropper(String path) {
		Image imageSampleLineChart = new Image(path);
		ChartConfigurator commonConfig = new ChartConfigurator(
				StandardColors.WHITE, 1);
		ChartCropper cropper = new ChartCropper(imageSampleLineChart);
		return cropper;
	}

	@Test
	public void testStartIdentification() {
		ChartCropper cropper = loadCropper("src/main/"
				+ "resources/TestCharts/LineCharts/Uncut/ExtractChartDataWithMainLines.png");
		Point expected = new Point(46, 40);
		Point actual = cropper.findChartStart();
		assertTrue(expected.equals(actual));
	}

	@Test
	public void testCenterIdentification_MultipleColorsOnMainLine() {
		Image imageSampleLineChart = new Image(
				"src/main/resources/TestCharts/LineCharts/Uncut/cpu_usage.png");
		ChartConfigurator commonConfig = new ChartConfigurator(
				StandardColors.WHITE, 1);
		ChartCropper cropper = new ChartCropper(imageSampleLineChart);
		Point actual = cropper.findChartStart();
		Point expected = new Point(28, 26);
		assertTrue(expected.equals(actual));
	}

	@Test
	public void testCenterIdentification_LineChartWithTCrossingOfAxises_AndLowHeightDominance() {
		Image imageSampleLineChart = new Image(
				"src/main/resources/TestCharts/LineCharts/Uncut/cpu_usage_all_metrics.png");
		ChartConfigurator commonConfig = new ChartConfigurator(
				StandardColors.WHITE, 1);
		ChartCropper cropper = new ChartCropper(imageSampleLineChart);
		Point actual = cropper.findChartStart();
		Point expected = new Point(36, 41);
		assertTrue(expected.equals(actual));
	}

	@Test
	public void testCenterIdentification_AnotherLineHasRepetitiveColor() {
		Image imageSampleLineChart = new Image(
				"src/main/resources/TestCharts/LineCharts/Uncut/Arisk_trend.png");
		ChartConfigurator commonConfig = new ChartConfigurator(
				StandardColors.WHITE, 1);
		ChartCropper cropper = new ChartCropper(imageSampleLineChart);
		Point actual = cropper.findChartStart();
		Point expected = new Point(45, 50);
		assertTrue(expected.equals(actual));
	}

	@Test
	public void testCenterIdentification_NewBarChart() {
		Image imageSampleLineChart = new Image(
				"src/main/resources/TestCharts/BarCharts/Uncut/OneBar.png");
		ChartConfigurator commonConfig = new ChartConfigurator(
				StandardColors.WHITE, 1);
		ChartCropper cropper = new ChartCropper(imageSampleLineChart);
		Point actual = cropper.findChartStart();
		Point expected = new Point(20, 127);
		assertTrue(expected.equals(actual));
	}

	@Test
	public void testCenterIdentification_StackedBars() {
		Image imageSampleLineChart = new Image(
				"src/main/resources/TestCharts/BarCharts/Uncut/StackedBars.png");
		ChartCropper cropper = new ChartCropper(imageSampleLineChart);
		ChartConfigurator commonConfig = new ChartConfigurator(
				StandardColors.WHITE, 1);
		Point actual = cropper.findChartStart();
		Point expected = new Point(20, 63);
		assertTrue(expected.equals(actual));
	}
	*/
}
