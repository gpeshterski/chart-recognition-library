package org.crl.imagedate;

import static org.junit.Assert.*;

import org.crl.charts.Chart;
import org.crl.imagedata.Image;
import org.junit.Test;

public class NoData {

	@Test(expected=IllegalStateException.class)
	public void testNoData() {
		Image image = new Image("src/test/resources/NoData/NoData.png");
	Chart chart=new Chart(image);
	    chart.containsData();

	}

	@Test(expected=IllegalStateException.class)
	public void testNoDataAvailable() {
		Image image = new Image("src/test/resources/NoData/NoDataAvailable.png");
		Chart chart=new Chart(image);
	    chart.containsData();
	}
}
