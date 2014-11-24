package org.crl.charts;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.map.HashedMap;
import org.crl.ChartExtractor;
import org.crl.charts.dataids.LegendColor;
import org.crl.charts.piechart.PieChart;
import org.crl.charts.piechart.PieChartConfig;
import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.utilities.Pair;
import org.crl.utilities.scaleparsing.fontutils.FontsDAL;
import org.junit.Test;

public class PieChartTest {
	@Test
	public void identifyMultipleLegendColors() {
		Image image = new Image(
				"src/test/resources/TestCharts/PieCharts/MultipleColors.png");
		PieChart pie = new PieChart(image);
		Vector<Color> actualColors = pie.findLegendColors();
		Vector<Color> expectedColors = new Vector<Color>();
		expectedColors.add(new Color(new int[] { 0, 134, 198 }));
		expectedColors.add(new Color(new int[] { 205, 104, 137 }));
		expectedColors.add(new Color(new int[] { 152, 251, 152 }));
		expectedColors.add(new Color(new int[] { 235, 235, 0 }));
		expectedColors.add(new Color(new int[] { 233, 150, 122 }));
		assertEquals(expectedColors, actualColors);

	}

	@Test
	public void identifySingleLegendColor() {
		Image image = new Image("src/test/resources/TestCharts/PieCharts/SingleColor.png");
		PieChart pie = new PieChart(image);
		Vector<Color> actualColors = pie.findLegendColors();
		Vector<Color> expectedColors = new Vector<Color>();
		expectedColors.add(new Color(new int[] { 0, 134, 198 }));
		assertEquals(expectedColors, actualColors);
	}

	@Test
	public void findColorPercentagesMultipleColors() {
		Image image = new Image(
				"src/test/resources/TestCharts/PieCharts/MultipleColors.png");
		PieChart pie = new PieChart(image);
		Vector<Pair<Color, Double>> actualPercentages = pie
				.getPercentagePerColors();
		Vector<Pair<Color, Double>> expectedPercentages = new Vector<Pair<Color, Double>>();
		expectedPercentages.add(new Pair<Color, Double>(new Color(new int[] {
				0, 134, 198 }), 30.33964160754673));
		expectedPercentages.add(new Pair<Color, Double>(new Color(new int[] {
				205, 104, 137 }), 20.142661073630897));
		expectedPercentages.add(new Pair<Color, Double>(new Color(new int[] {
				152, 251, 152 }), 9.723376698691307));
		expectedPercentages.add(new Pair<Color, Double>(new Color(new int[] {
				235, 235, 0 }), 30.33964160754673));
		expectedPercentages.add(new Pair<Color, Double>(new Color(new int[] {
				233, 150, 122 }), 9.723376698691307));
		for (int i = 0; i < 5; i++) {
			assertEquals(expectedPercentages.get(i).getSecond(),
					actualPercentages.get(i).getSecond(), 1);
		}
	}

	@Test
	public void findColorPercentagesSingleColor_RawClasses() {
		Image image = new Image("src/test/resources/TestCharts/PieCharts/SingleColor.png");
		PieChart pie = new PieChart(image);
		Vector<Pair<Color, Double>> actualPercentages = pie
				.getPercentagePerColors();
		Vector<Pair<Color, Double>> expectedPercentages = new Vector<Pair<Color, Double>>();
		expectedPercentages.add(new Pair<Color, Double>(new Color(new int[] {
				0, 134, 198 }), 100.0));

	}
	@Test
	public void findColorPercentagesMultipleColor_WrappedClasses(){
		Image image = new Image(
				//"src/test/resources/TestCharts/PieCharts/MultipleColors.png");
		"src/test/resources/TestCharts/PieCharts/ThreeD/3DTest2.png");
		ChartExtractor extractor=new ChartExtractor("DejaVMUni");
		PieChartConfig config=new PieChartConfig();
		
	/*	config.setChartColor(new Color(new int[] {
				235, 235, 0 }));*/
		
		Vector<Pair<LegendColor, Double>> data = extractor.getPieChartData(image, config);
		/*assertEquals(30,data.get(0).getSecond(),0.2);*/
	}
	@Test
	public void pieTest(){
		Image image=new Image("src/test/resources/TestCharts/PieCharts/test.png");
		PieChart pie=new PieChart(image);
		ChartExtractor extractor=new ChartExtractor(ChartExtractor.DEFAULT_FONT);
	PieChartConfig config=new PieChartConfig();
	config.setChartColor(new Color(new int[]{192,80,77}));
	Vector<Pair<LegendColor, Double>> extractedData = extractor.getPieChartData(image, config);
		/*for(int i=0;i<data.size();i++){
			System.out.println(data.get(i).getFirst()+"  "+data.get(i).getSecond());
		}*/
	}


}
