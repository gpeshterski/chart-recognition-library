package org.crl.utilities;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.scaleparsing.NumberParser;


/**
 * Recognizes the position and reads the text content of scale labels
 * */
public class ScaleRecognizer {
	private Image image;
	/**
	 * The image after converting to monochrome and clearing too different
	 * pixels
	 * */
	private Image clearedImage;
	Point chartStart;
	private ChartConfigurator commonConfig;

	/**
	 * @param A
	 *            non cropped image
	 * @param The
	 *            chartStart from the ChartCropper for example
	 */
	public ScaleRecognizer(Image image, Point chartStart,
			ChartConfigurator commonConfig) {
		this.image = image;
		this.clearedImage = image.monochromize();
		this.chartStart = chartStart;
		this.commonConfig = commonConfig;
	}

	/**
	 * Finds the appropriate left text region and reads the contained text
	 * 
	 * @param position
	 *            the right middle of the text region
	 * */
	public String readLeftText(Point position) {
		Pair<Integer, Integer> dimensions = findLargestLeftTextRegion(position);
		int areaWidth = Math.max(8, dimensions.getFirst());
		int areaHeight = Math.max(8, dimensions.getSecond() + 2);
		Point start = new Point(Math.max(position.getXCoord() - areaWidth, 0),
				Math.min(position.getYCoord() + areaHeight / 2,
						image.getHeight()));
		Image toRecognize = image.cropArea(start, areaWidth - 1, areaHeight).duplicate().duplicate();
//		ImageUtils.saveImage(toRecognize.getInnerImage(), "c:\\temp\\torecognize");
		String textRecognized = OCRReader.recognizeYText(toRecognize);
		return textRecognized;
	}

	/**
	 * Validates that the selected scrapers are increasing First element in the
	 * pair is the value Second element is the pixel position Chooses the
	 * required scrapers to recognize the scale Two for linear scale Three for
	 * logarithmic
	 **/
	Vector<Pair<Comparable, Integer>> getRequiredScaleScrapers(
			Vector<Pair<Comparable, Integer>> longestScrapersPositioned) {
		Vector<Pair<Comparable, Integer>> resultScrapers = new Vector<Pair<Comparable, Integer>>();
		// the worst case is obviously o(n^2) the average-linear or better
		// for less than 50 elements not important
		for (int i = 0; i < longestScrapersPositioned.size() - 1; i++) {
			Comparable lowValue = longestScrapersPositioned.get(i).getFirst();
			for (int j = i + 1; j < longestScrapersPositioned.size(); j++) {
				Comparable highValue = longestScrapersPositioned.get(j)
						.getFirst();
				if (lowValue.compareTo(highValue) < 0) {
					resultScrapers.add(longestScrapersPositioned.get(i));
					resultScrapers.add(longestScrapersPositioned.get(j));
					return resultScrapers;
				}
			}
		}
		throw new IllegalStateException(
				"The scale recognition failed. Please use the manual configuration");
	}

	/**
	 * Tries to find the largest possible zone containing mostly text
	 * 
	 * @return A point where xCoord is the width yCoord is the height
	 * @param The
	 *            point where the scraper was found e.g the rightmost center
	 * 
	 */
	private Pair<Integer, Integer> findLargestLeftTextRegion(Point start) {
		int maxSearchWidth = 70;
		int maxSearchHeight = 30;
		int minFoundX = image.getWidth();
		int maxFoundHeight = 0;
		Pair<Integer, Integer> dimensions;
		for (int x = start.getXCoord(); x >= Math.max(0, start.getXCoord()
				- maxSearchWidth); x--) {
			for (int height = 1; height <= maxSearchHeight / 2; height++) {
				if ((!image
						.getPixel(
								x,
								Math.min(start.getYCoord() + height,
										image.getHeight() - 1)).getColor()
						.isEqualTo(commonConfig.getBackgroundColor()) || (!image
						.getPixel(x, Math.max(start.getYCoord() - height, 0))
						.getColor()
						.isEqualTo(commonConfig.getBackgroundColor())))) {
					if (x < minFoundX) {
						minFoundX = x;

					}
					if (height > maxFoundHeight) {
						maxFoundHeight = height;
					}
				}

			}
		}
		dimensions = new Pair(Math.max((start.getXCoord() - minFoundX) + 2, 0),
				(maxFoundHeight * 2) + 2);
		return dimensions;
	}

	private Pair<Integer, Integer> findLargestLowerTextRegion(Point start) {
		int maxAreaWidth = 70;
		int maxAreaHeight = 60;
		int maxFoundWidth;
		int maxFoundHeight;
		// identifies lines of text, legend is at maximum of two
		Pair<Integer, Integer> dimensions = null;
		for (int x = 1; x <= Math.min(maxAreaWidth / 2, start.getXCoord() - 1); x++) {
			for (int y = 1; y <= Math.min(maxAreaHeight, start.getYCoord() - 1); y++) {

			}
		}
		return dimensions;

	}

	/**
	 * Reads a label sized text region under the point
	 * 
	 * @param position
	 *            the higher middle point of the region
	 * */
	public String readLowerText(Point position) {
		int areaWidth = 60;
		int areaHeight = 50;
		Point start = new Point(Math.max(position.getXCoord() - areaWidth / 2,
				0), position.getYCoord());
		Image iamgeLabel = image.cropArea(start, areaWidth, areaHeight);
//		ImageUtils.saveImage(iamgeLabel.getInnerImage(), "c:\\temp\\xLabel");
		String textRecognized = OCRReader.recognizeXText(iamgeLabel);
		return textRecognized;
	}

	/**
	 * Tries to find the scale scrapers
	 * 
	 * @return scrapers in a sorted order from lowest to highest
	 */
	public Map<Integer, Integer> findScaleLabelScrapers() {
		int scraperPosition = clearedImage.getHeight();
		Map<Integer, Integer> scrapers = new HashMap<Integer, Integer>();
		for (int yCoord = clearedImage.getHeight() - 1; yCoord >= chartStart
				.getYCoord(); yCoord--) {
			int xCoord = chartStart.getXCoord();
			int width = 0;
			// this is not background
			if (!clearedImage.getPixel(xCoord, yCoord).getColor()
					.isEqualTo(StandardColors.MONO_WHITE)) {
				do {
					Color current = clearedImage.getPixel(xCoord - width,
							yCoord).getColor();
					width++;
					// the scraper ends at the image end
					if (xCoord - width < 0) {
						break;
					}
					Color next = clearedImage.getPixel(xCoord - width, yCoord)
							.getColor();
					// the possible scraper ends
					if (!current.isEqualTo(next)) {
						break;
					}
				} while (true);
				// adding to scrapers
				scrapers.put(yCoord - chartStart.getYCoord(), width);
			}
		}
		return MapUtilities.sortByValues(scrapers);
	}

	public Map<Integer, Integer> getLongestXScaleScrapers() {
		Map<Integer, Integer> longestScrapers = MapUtilities
				.sortByKeys(MapUtilities
						.getLongestScrapers(findXLabelScrapers()));
		return longestScrapers;
	}

	/**
	 * Generates a list of all labels with values
	 * */
	public Vector<Pair<Comparable, Integer>> getScaleLabels() {
		// the longest scrapers are at the front
		Map<Integer, Integer> longestScrapers = MapUtilities
				.getLongestScrapers(findScaleLabelScrapers());

		// left contains value, right contains pixelPosition
		Vector<Pair<Comparable, Integer>> longestScrapersPositioned = new Vector<Pair<Comparable, Integer>>();

		for (Integer yCoord : longestScrapers.keySet()) {
			int width = longestScrapers.get(yCoord);
			String scaleResult = readLeftText(new Point(chartStart.getXCoord()
					- width - 1, chartStart.getYCoord() + yCoord));
			double value = new NumberParser(scaleResult, 4).getNumber();
			if (value != Double.MIN_VALUE) {
				longestScrapersPositioned.add(new Pair(value, yCoord));
			}
		}
		return longestScrapersPositioned;
	}

	/**
	 * Tries to find the x scale scrapers the scraper positioning starts from
	 * the chart start
	 * 
	 * @return scrapers in a sorted order from lowest to highest longest
	 *         scrapers at the front
	 */
	public Map<Integer, Integer> findXLabelScrapers() {
		int scraperPosition = clearedImage.getWidth();
		Map<Integer, Integer> scrapers = new HashMap<Integer, Integer>();
		for (int xCoord = clearedImage.getWidth() - 1; xCoord >= chartStart
				.getXCoord(); xCoord--) {
			int yCoord = chartStart.getYCoord();
			int height = 0;
			// this is not background
			if (!clearedImage.getPixel(xCoord, yCoord).getColor()
					.isEqualTo(StandardColors.MONO_WHITE)) {
				do {
					Color current = clearedImage.getPixel(xCoord,
							yCoord - height).getColor();
					height++;
					// the scraper ends at the image end
					if (yCoord - height < 0) {
						break;
					}
					Color next = clearedImage.getPixel(xCoord, yCoord - height)
							.getColor();
					// the possible scraper ends
					if (!current.isEqualTo(next)) {
						break;
					}
				} while (true);
				// adding to scrapers
				scrapers.put(xCoord, height);
			}
		}
		return MapUtilities.sortByValues(scrapers);
	}

	/**
	 * Extracts the time label values automatically
	 * */
	public Vector<Pair<String, Integer>> getTimeLabelStrings() {

		// the longest scrapers are at the front
		int maxWidth = 0;
		Long lowDate;
		Long highDate;
		Map<Integer, Integer> longestScrapers = MapUtilities
				.getLongestScrapers(findXLabelScrapers());

		// left contains value, right contains pixelPosition
		Vector<Pair<String, Integer>> longestScrapersPositioned = new Vector<Pair<String, Integer>>();

		for (Integer xCoord : longestScrapers.keySet()) {
			int width = longestScrapers.get(xCoord);
			String dateResult = readLowerText(new Point(xCoord,	chartStart.getYCoord() - width));

			if (!(dateResult.equals(""))) {
				// date parse was successful
				longestScrapersPositioned.add(new Pair(dateResult, xCoord));
			}
		}
		return longestScrapersPositioned;
	}

	/**
	 * Matches the given extremal values to the first and last scraper
	 * */
	private Vector<Pair<Comparable, Integer>> getMetricValueLabels(
			Comparable metricLowValue, Comparable metricHighValue,
			Map<Integer, Integer> scrapers) {
		Vector<Pair<Comparable, Integer>> valueLabels = new Vector<Pair<Comparable, Integer>>();
		int metricLow = Integer.MAX_VALUE;
		int metricHigh = 0;
		int width = 0;
		Iterator it = scrapers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if ((Integer) pair.getKey() < metricLow) {
				metricLow = (Integer) pair.getKey();
			}
			if (metricHigh < (Integer) pair.getKey()) {
				metricHigh = (Integer) pair.getKey();
			}
		}

		valueLabels
				.add(new Pair<Comparable, Integer>(metricLowValue, metricLow));

		valueLabels.add(new Pair<Comparable, Integer>(metricHighValue,
				metricHigh));
		return valueLabels;

	}

	/**
	 * Calculates the metric time values of the scraper positions, given the
	 * starting and ending time of the interval(at the first and last
	 * distinguishable scrapers)
	 * */
	public Vector<Pair<Comparable, Integer>> getTimeLabels(
			Calendar timeLowValue, Calendar timeHighValue) {
		return getMetricValueLabels(timeLowValue.getTimeInMillis(),
				timeHighValue.getTimeInMillis(), findXLabelScrapers());
	}

	/**
	 * Calculates the metric scale values of the scraper positions, given the
	 * starting and ending value of the interval(at the first and last
	 * distinguishable scrapers)
	 * */
	public Vector<Pair<Comparable, Integer>> getScaleLabels(
			Double scaleLowValue, Double scaleHighValue) {
		Map<Integer, Integer> scales = findScaleLabelScrapers();
		Map<Integer, Integer> scalesSorted = MapUtilities
				.getLongestScrapers(scales);
		return getMetricValueLabels(scaleLowValue, scaleHighValue, scalesSorted);
	}
}
