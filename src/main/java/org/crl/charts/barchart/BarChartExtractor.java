package org.crl.charts.barchart;

import java.util.Vector;

import org.crl.charts.Chart;
import org.crl.imagedata.Color;
import org.crl.imagedata.ColorUtilities;
import org.crl.imagedata.Image;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.ChartConfigurator;
/**
 * Extracts the values of the bars when their positions on the scale are known
 * */
public class BarChartExtractor extends Chart {

	private Image clearedImage;
	private int barsCount;
	/*
	 * The lower center of the bars Calculation through formulas is not
	 * preferred since it is possible that not all slots contain bars
	 */
	private Vector<Integer> barCenters;
	private BarChartAxisConfiguration chartConfig;
	//the monochrome filter turns bar color into black
	private Color barColor = StandardColors.BLACK;
    private ColorUtilities utilities;
	public BarChartExtractor(Image image, BarChartAxisConfiguration chartConfig,ChartConfigurator commonConfig,
			Vector<Integer> barCenters) {
		super(image.monochromize(),commonConfig);
		this.chartConfig = chartConfig;
		this.barCenters = barCenters;
		this.barsCount = barCenters.size();
		utilities=new ColorUtilities(image);
	}

	/**
	 * Enumeration starts from zero
	 * @return the xCoord of the bar with the desired index
	 */
	public Integer getBarCenter(int barIndex) {
		if (barIndex >= barsCount) {
			throw new IndexOutOfBoundsException(
					"The bars are fewer than the entered index");
		}
		return barCenters.elementAt(barIndex);
	}

	/**
	 * Performs a search to find the height of a bar
	 */
	private int findBarHeight(int barIndex) {
		if (barIndex >= barsCount) {
			throw new IndexOutOfBoundsException(
					"The bars are fewer than the entered index");
		}
		int heightResult = 0;
		int columnNumber = getBarCenter(barIndex);
		
		for(int i=getCommonConfig().getMainLineWidth();i<getImage().getHeight()-1;i++)
			for(int j=columnNumber-5;j<columnNumber+5;j++){
				if(!getImage().getPixel(j, i).getColor().isEqualTo(StandardColors.BLACK)){
					return i;
				}
			}
	return 0;
	}

	/**
	 * Produces a vector with the heights of the bars in pixels
	 */
	public Vector<Integer> getAllBarsHeightsInPixels() {
		Vector<Integer> barHeights = new Vector<Integer>();
		for (int barIndex = 0; barIndex < barsCount; barIndex++) {
			int height = findBarHeight(barIndex);
			barHeights.add(height);
		}
		return barHeights;
	}
   /**
    * Produces a vector with the heights of the bars in actual double values 
    * */
	public Vector<Double> getAllBarsHeights() {
		Vector<Integer> barHeightsInPixels = getAllBarsHeightsInPixels();
		Vector<Double> barHeights = new Vector<Double>();
		for (int i = 0; i < barHeightsInPixels.size(); i++) {
			barHeights.add(chartConfig.getScaleValue(barHeightsInPixels
					.elementAt(i)));
		}
		return barHeights;
	}
}
