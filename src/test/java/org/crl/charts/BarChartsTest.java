package org.crl.charts;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;

import org.crl.ChartExtractor;
import org.crl.DataMatcher;
import org.crl.charts.barchart.BarChart;
import org.crl.charts.barchart.BarChartAxisConfiguration;
import org.crl.charts.barchart.BarChartConfig;
import org.crl.charts.barchart.BarChartDistanced;
import org.crl.charts.barchart.BarChartExtractor;
import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.ChartConfigurator;
import org.crl.utilities.Pair;
import org.crl.utilities.ScaleRecognizer;
import org.crl.utilities.scaleparsing.NumberSequenceValidator;
import org.crl.utilities.scaleparsing.fontutils.FontsDAL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BarChartsTest {
	private void testBarChart_RawClasses(int i) {
		Image image = new Image("src/test/resources/TestCharts/BarCharts/Uncut/cpu.png");
		ChartCropper cropper = new ChartCropper(image);
		ChartConfigurator commonConfig = new ChartConfigurator(StandardColors.WHITE, 1);
		ScaleRecognizer recognizer = new ScaleRecognizer(image, cropper.getChartStart(), commonConfig);
		FontsDAL font = new FontsDAL("DejaVMUni");
		Vector<Pair<Comparable, Integer>> scaleLabels = recognizer.getScaleLabels();
		NumberSequenceValidator scaleValidator = new NumberSequenceValidator(scaleLabels, font.getDigitErrors());
		YAxisConfiguration yAxis = new YAxisConfiguration(scaleValidator.getValidatedScale(scaleValidator.getValidSequence()));
		BarChartAxisConfiguration chartConfig = new BarChartAxisConfiguration(yAxis);
		BarChart chart = new BarChart(image, chartConfig,commonConfig);
		Vector<Double> data = chart.extractChart();
		for (int j = 0; j < data.size(); j++) {
			System.out.println(data.get(j));
		}
	}

	@Test
	public void testBarCharts_WrappedClasses(){
		ChartExtractor extractor=new ChartExtractor(ChartExtractor.DEFAULT_FONT);
		BarChartConfig config=new BarChartConfig();
		List<Vector<Pair<String,Double>>> expectedData=new LinkedList<Vector<Pair<String,Double>>>();
		Vector<Pair<String,Double>> expected1=new Vector<Pair<String,Double>>();
		expected1.add(new Pair<String, Double>("", 4.0));
		expected1.add(new Pair<String, Double>("", 0.0));
		expected1.add(new Pair<String, Double>("", 0.0));
		expected1.add(new Pair<String, Double>("", 0.0));
		expected1.add(new Pair<String, Double>("", 0.0));
		expectedData.add(expected1);
		
		Vector<Pair<String,Double>> expected2=new Vector<Pair<String,Double>>();
		expected2.add(new Pair<String, Double>("", 4.0));
		expectedData.add(expected2);
		
		Vector<Pair<String,Double>> expected3=new Vector<Pair<String,Double>>();
		expected3.add(new Pair<String, Double>("", 1.0));
		expected3.add(new Pair<String, Double>("", 0.0));
		expected3.add(new Pair<String, Double>("", 0.0));
		expected3.add(new Pair<String, Double>("", 0.0));
		expected3.add(new Pair<String, Double>("", 0.0));
		expected3.add(new Pair<String, Double>("", 0.0));
		expected3.add(new Pair<String, Double>("", 1.0));
		expected3.add(new Pair<String, Double>("", 0.0));
		expected3.add(new Pair<String, Double>("", 0.0));
		expected3.add(new Pair<String, Double>("", 2.0));
		expectedData.add(expected3);
		
		for(int i=1;i<=3;i++){
		Image image = new Image(
				"src/test/resources/TestCharts/BarCharts/Uncut/"+i+".png");
		Vector<Pair<String,Double>> actualData=extractor.getBarChartData(image, config);
		assertTrue(DataMatcher.isBarChartSimilar(actualData, expectedData.get(i-1), 95));
			}
	}
	@Test
	public void testImage(){
		ChartExtractor extractor=new ChartExtractor(ChartExtractor.DEFAULT_FONT);
		BarChartConfig config=new BarChartConfig();
		Image image = new Image("src/test/resources/TestCharts/BarCharts/Uncut/"+2+".png");
		Vector<Pair<String,Double>> actualData=extractor.getBarChartData(image, config);
	assertEquals(actualData.get(0).getSecond(),4.0,0.1);
	}
	@Test 
	public void testDistanced(){
	Image image=new Image("src/test/resources/TestCharts/BarCharts/Uncut/host_mem_capacity.png");
	ChartExtractor extractor=new ChartExtractor(ChartExtractor.DEFAULT_FONT);
	}
}
