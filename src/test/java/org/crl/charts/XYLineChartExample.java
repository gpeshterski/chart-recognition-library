package org.crl.charts;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * This program demonstrates how to draw XY line chart with XYDataset
 * using JFreechart library.
 * @author www.codejava.net
 *
 */
public class XYLineChartExample extends ApplicationFrame  {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XYLineChartExample(XYDataset dataset) {
    	super("sample");
    	 final JFreeChart chart = ChartFactory.createTimeSeriesChart(
    	            "Time Series Demo 8", 
    	            "Date", 
    	            "Value",
    	            dataset, 
    	            true, 
    	            true, 
    	            false
    	        );
    	        final XYItemRenderer renderer = chart.getXYPlot().getRenderer();
    	        final StandardXYToolTipGenerator g = new StandardXYToolTipGenerator(
    	            StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
    	            new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")
    	        );
    	        
    	        XYPlot plot = (XYPlot) chart.getPlot();
    	        DateAxis axis = (DateAxis) plot.getDomainAxis();
    	        axis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yy HH:mm a")); 
    	        
    	        final ChartPanel chartPanel = new ChartPanel(chart);
    	        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    	        chartPanel.setMouseZoomable(true, false);
    	        setContentPane(chartPanel);
    	        
    	        this.pack();
    	        RefineryUtilities.centerFrameOnScreen(this);
    	        this.setVisible(true);

    }
 

    
}