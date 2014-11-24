package org.crl.charts.linechart;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crl.charts.Chart;
import org.crl.exceptions.ColorNotFoundException;
import org.crl.imagedata.Color;
import org.crl.imagedata.ColorUtilities;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.PointComparator;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.ChartConfigurator;
import org.crl.utilities.MapUtilities;

/**
 * Extracts the data for a line chart image
 * */
public class LineChart extends Chart {
	private static final Log logger = LogFactory.getLog(LineChart.class);
	private LineChartConfig config;
	private ColorUtilities utilities;
	/**
	 * the minimal chart line length in part of the whole image width for the
	 * chart to be considered line chart the default value is very since it is
	 * used when a configuration says that we deal with a line chart
	 */
	private double minChartLineLength = 1 / 500.0;
	/**
	 * Used to prevent confusion where the line is very thin
	 * */
	private Color defaultExtremesColor = new Color(new int[] { 251, 138, 60 });

	/**
	 * @param croppedImage
	 *            the image starting from the axis
	 * @param commonConfig
	 *            -Universal chart configuration properties for axised charts
	 *            including background color and main axis line width
	 * @param config
	 *            Includes the properties of a line chart that might be set
	 *            manually or identified by an algorithm automatically
	 * 
	 * 
	 * */
	public LineChart(Image croppedImage, ChartConfigurator commonConfig,
			LineChartConfig config) {
		super(croppedImage, commonConfig);
		utilities = new ColorUtilities(croppedImage);
		this.config = config;

		if (this.config.getExtremalsColor() == null) {
			config.setExtremalsColor(findExtremesColor());
		}
		if (this.config.getChartLineColor() == null) {
			/*
			 * The given image might not be of line chart
			 */
			minChartLineLength = 1 / 7.0;
			config.setChartLineColor(findLineColor());

		} else {
			/*
			 * Otherwise it is definite that the chart is a line chart and any
			 * length is acceptable
			 */}

	}

	/**
	 * Finds the starting position of the chart with desired color
	 * Starting from the lower left corner of the chart moving up and right
	 * */
	public Point findChartStart() {
		Point chartStart;
		for (int xCoord = getCommonConfig().getMainLineWidth(); xCoord < getImage()
				.getWidth(); xCoord++) {
			for (int yCoord = getCommonConfig().getMainLineWidth(); yCoord < getImage()
					.getHeight(); yCoord++) {

				Color current = getImage().getPixel(xCoord, yCoord).getColor();
				if (current.isComparableTo(config.getChartLineColor())) {
					chartStart = new Point(xCoord, yCoord);
					if (utilities.isRegionRectangle(xCoord, yCoord, 10)) {
						throw new IllegalStateException("Not a line chart");
					}
					return chartStart;
				}
			}
		}

		throw new ColorNotFoundException(
				ColorNotFoundException.LINE_COLOR_NOT_FOUND);
	}

	/**
	 * Finds the color of extremal points in case they are marked
	 * */
	private Color findExtremesColor() {
		try {/*
			 * extremes circles are at the beginning of the chart
			 */
			for (int i = getCommonConfig().getMainLineWidth(); i < Math.min(50,
					getImage().getWidth() / 10); i++) {
				for (int j = getImage().getHeight(); j >= 5; j--) {
					if (utilities.isRegionRectangle(i, j, 5)) {
						if (!getImage()
								.getPixel(i, j)
								.getColor()
								.isComparableTo(
										getCommonConfig().getBackgroundColor())) {
							return getImage().getPixel(i, j).getColor();
						}
					}
				}
			}
		} catch (Exception ex) {
			// in case something failed we return the most likely color
		}
		return defaultExtremesColor;
	}

	/**
	 * Finds the first likely candidate for line color if one is not marked
	 * 
	 * @return null if no appropriate candidate was defined
	 * */
	private Color findLineColor() {
		Map<Color, Integer> imageDominanceMap = new HashMap<Color, Integer>();
		for (int i = 0; i < getImage().getWidth(); i++) {
			Map<Color, Integer> dominanceMap = new HashMap<Color, Integer>();
			for (int j = 0; j < getImage().getHeight(); j++) {
				utilities.addToDominanceMap(dominanceMap, new Point(i, j));
			}
			// this is probably a scale line
			if (!utilities.hasSufficientDominance(dominanceMap, getImage()
					.getHeight() / 3)) {
				MapUtilities.mergeMaps(imageDominanceMap, dominanceMap);
			}
		}
		// optimal height enough to define any horizontal axis lines
		for (int i = 0; i < getImage().getHeight() / 8; i++) {
			Map<Color, Integer> dominanceInRow = new HashMap<Color, Integer>();
			for (int j = 0; j < getImage().getWidth(); j++) {
				utilities.addToDominanceMap(dominanceInRow, new Point(j, i));
			}
			// this is a likely candidate for an axis line color
			Color dominating = utilities.getDominatingColor(dominanceInRow,
					getImage().getWidth() / 2);

			if (dominating != null && imageDominanceMap.containsKey(dominating)) {
				imageDominanceMap.remove(dominating);
			}
		}
		imageDominanceMap.remove(config.getExtremalsColor());
		Map<Color, Integer> vals = MapUtilities.sortByValues(imageDominanceMap);

		Iterator it = vals.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Color candidate = (Color) pair.getKey();
			// grey is generally color for technical purposes
			if (!(candidate).isGrey(15)) {
				return candidate;
			}
		}
		// no appropriate candidate was defined
		return null;
	}
/**
 * Adds a point to the queue of waiting to become part of the chart
 * */
	private void addPointToWaiting(Point candidate, Queue<Point> waiting,
			HashSet<Point> visitedPoints) {
		// candidate is in the chart
		if (candidate != null) {
			// has not been visited
			if (!visitedPoints.contains(candidate)) {
				// is of corresponding color
				if (getImage().getPixel(candidate).getColor()
						.isComparableTo(config.getChartLineColor())) {
					waiting.add(candidate);
					visitedPoints.add(candidate);
				}
			}
		}
	}

	/**
	 * Extracts the pixel values of the chart line
	 * through tracing the chart line
	 * */
	private Vector<Point> extractChartPoints() {
		HashSet<Point> visitedPoints = new HashSet<Point>();

		Queue<Point> waiting = new LinkedList<Point>();

		Vector<Point> extractedPoints = new Vector<Point>();

		Point initialPosition = null;

		initialPosition = findChartStart();

		waiting.add(initialPosition);

		while (true) {
			traceChartLine(visitedPoints, waiting, extractedPoints);
			// the chart was interrupted by an extremal point or has ended
			// before the image end
			Point interruptionPoint = extractedPoints.lastElement();
			Point endOfInterruption = skipExtremalPoint(interruptionPoint);

			// the chart line has ended
			if (endOfInterruption == null) {
				break;
			}
			addContinuationLine(interruptionPoint, endOfInterruption,
					extractedPoints);
			waiting.add(endOfInterruption);
		}

		return extractedPoints;
	}

	/**
	 * The extremal introduces a large point where no data will be available. To
	 * compensate we add a straight line between interruptions
	 * */
	private void addContinuationLine(Point interruptionPoint,
			Point endOfInterruption, Vector<Point> extractedPoints) {

		int difference = endOfInterruption.getYCoord()
				- interruptionPoint.getYCoord();
		int interval = endOfInterruption.getXCoord()
				- interruptionPoint.getXCoord();
		int step = difference / interval;
		int yCoordPredicted = interruptionPoint.getYCoord();
		for (int posX = interruptionPoint.getXCoord(); posX < endOfInterruption
				.getXCoord(); posX++) {
			yCoordPredicted += step;
			Point injectionPoint = new Point(posX, yCoordPredicted);
			extractedPoints.add(injectionPoint);
		}
	}

	/**
	 * Return the point where the chart continues if there was an extremal or
	 * null if the chart line has ended
	 * */
	private Point skipExtremalPoint(Point interruptionPoint) {
		Point skippingPoint;
		int imageEndBoarder = getImage().getWidth()
				- interruptionPoint.getXCoord() - 1;
		// the maximal diameter of an extremal point
		int maxPossibleExtremalPointSize = 25;
		int maxSkippingArea = Math.min(imageEndBoarder,
				maxPossibleExtremalPointSize);
		int startXCoord = interruptionPoint.getXCoord();
		int startYCoord = interruptionPoint.getYCoord();
		// tries a reasonably sized interruption region
		for (int xCoord = startXCoord + 1; xCoord < startXCoord
				+ maxSkippingArea; xCoord++) {
			for (int yCoord = startYCoord - maxPossibleExtremalPointSize; yCoord < startYCoord
					+ maxPossibleExtremalPointSize; yCoord++) {
				if (getImage().getPixel(xCoord, yCoord).getColor()
						.isComparableTo(config.getChartLineColor())) {
					skippingPoint = new Point(xCoord, yCoord);

					return skippingPoint;
				}
			}
		}
		return null;
	}

	/**
	 * Signalizes when the chart line was interrupted - because of extremal
	 * point found or end of chart
	 */
	private void traceChartLine(HashSet<Point> visitedPoints,
			Queue<Point> waiting, Vector<Point> extractedPoints) {
		while (!waiting.isEmpty()) {
			ChartTraveler traveller = new ChartTraveler(waiting.poll(),
					getCommonConfig().getMainLineWidth(),
					getImage().getWidth(), getImage().getHeight());
			extractedPoints.add(traveller.getPosition());

			Point higher = traveller.getHigher();
			addPointToWaiting(higher, waiting, visitedPoints);

			Point lower = traveller.getLower();
			addPointToWaiting(lower, waiting, visitedPoints);

			Point forwarder = traveller.getForwarder();
			addPointToWaiting(forwarder, waiting, visitedPoints);
		}
	}

	/**
	 * The chart line has width, we need the average of the value of all points
	 * marking specific time
	 * */
	public Vector<Point> averageChartValues() {
		Vector<Point> extractedPoints = extractChartPoints();

		Vector<Point> averagedPoints = new Vector<Point>();

		Collections.sort(extractedPoints, new PointComparator());
		for (int index = 0; index < extractedPoints.size() - 1; index++) {
			int count = 0;
			int sumOfValues = 0;

			do {
				count++;
				sumOfValues += extractedPoints.elementAt(index).getYCoord();
				if (index >= extractedPoints.size() - 1) {
					break;
				}
				// we do not compensate too many points into one-this would harm
				// peaks
				if (count > getImage().getHeight() / 10) {
					break;

				}
			} while (extractedPoints.elementAt(index).getXCoord() == extractedPoints
					.elementAt(++index).getXCoord());

			int averageY = sumOfValues / count;

			averagedPoints.add(new Point(extractedPoints.elementAt(index - 1)
					.getXCoord(), averageY));
			index--;
		}
		return averagedPoints;
	}

	/**
	 * Extracts the timeStamp value pairs corresponding to the line chart values
	 * one value per pixel width.
	 * */
	public Map<Long, Double> extractTimeStampsValuePairs() {

		Map<Long, Double> resultStamps = new HashMap<Long, Double>();

		Long time;
		double value;
		Vector<Point> extractedPoints = averageChartValues();
		if (extractedPoints.size() < (int) ((double) getImage().getWidth() * minChartLineLength)) {
			throw new IllegalStateException("Too short line chart found.");
		}
		for (Point point : extractedPoints) {
			time = config.getAxisConfig().getTimeValue(point);
			value = config.getAxisConfig().getScaleValue(point);
			resultStamps.put(time, value);
		}
		return resultStamps;
	}
}
