package org.crl.charts;

import java.util.ArrayList;
import java.util.Vector;

import org.crl.charts.piechart.PieChart;
import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.utilities.Pair;
import org.junit.Test;

public class PieChart3DTest {

	@Test
	public void PieChart3D() {
		ArrayList<Integer> tests=new ArrayList<Integer>();
		for (int i = 2; i <= 4; i++) {
			System.out.println("3D Pie Test" + i);
			Image image = new Image(
					"src/test/resources/TestCharts/PieCharts/ThreeD/3DTest" + i
							+ ".png");
			org.crl.charts.piechart.PieChart3D pieChart = new org.crl.charts.piechart.PieChart3D(
					image);
			Vector<Pair<Color, Double>> result = pieChart.extractPieData();
			for(Pair<Color,Double> p:result){
				System.out.println(p.getFirst()+ "  "+ p.getSecond());
			}
		}
	}
}
