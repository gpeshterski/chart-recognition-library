package org.crl.charts.piechart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.crl.charts.Chart;
import org.crl.charts.dataids.LegendColor;
import org.crl.imagedata.Color;
import org.crl.imagedata.ColorUtilities;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.Pair;

/**
 * Extracts the percentage from a 3D Pie chart represented as a cylinder
 * */
public class PieChart3D extends Chart {
	private ColorUtilities utilities;
	private Color backgroundColor = StandardColors.WHITE;
	private Color lineColor = StandardColors.BLACK;
	/**
	 * The wide radius of the pie ellipse
	 * */
	private int bigRadius;
	/**
	 * The small radius of the ellipse
	 * */
	private int smallRadius;
	/**
	 * The point denoting the center of the ellipse
	 * */
	private Point center;

	public PieChart3D(Image image) {
		super(image);
		utilities = new ColorUtilities(image);
		findBigRadiusAndCenter();
		findSmallRadius();
	}

	/**
	 * @return the two points limiting the diameter of the pie
	 * */
	private Pair<Point, Point> findPieDiameter() {
		Pair<Point, Point> diameterLimiters = new Pair<Point, Point>(new Point(
				0, 0), new Point(0, 0));
		int regionSize = 6;
		int diameterCandidate = 0;
		for (int y = getImage().getHeight() - regionSize; y > regionSize; y--) {
			for (int x = 1; x < getImage().getWidth() - 1; x++) {
				int start = x;
				while (isRegionDense(new Point(x, y), regionSize)) {
					if (x > getImage().getWidth() - 2) {
						break;
					}
					x++;
				}
				if (x - start > diameterCandidate) {
					diameterCandidate = x - start;
					diameterLimiters.setFirst(new Point(start, y));
					diameterLimiters.setSecond(new Point(x, y));
				}
			}
		}
		return diameterLimiters;
	}

	/**
	 * Validates whether the region is part of an image and there is little
	 * background
	 * */
	private boolean isRegionDense(Point point, int regionSize) {
		int backCount = 0;
		for (int h = point.getYCoord() - regionSize / 2; h <= point.getYCoord()
				+ regionSize / 2; h++) {

			if (getImage().getPixel(point.getXCoord(), h).getColor()
					.isComparableTo(backgroundColor, 80)) {
				backCount++;
			}
		}
		// there is too much background in the region
		if (backCount > regionSize / 5) {
			return false;
		}
		return true;
	}

	/**
	 * Finds the widest area representing the desired ellipse
	 * */
	private void findBigRadiusAndCenter() {
		boolean precisedCenter = false;
		Pair<Point, Point> diameterLimiters = findPieDiameter();
		this.bigRadius = (diameterLimiters.getSecond().getXCoord() - diameterLimiters
				.getFirst().getXCoord()) / 2;

		this.center = new Point(this.bigRadius
				+ diameterLimiters.getFirst().getXCoord(), diameterLimiters
				.getFirst().getYCoord());
		int regionSize = 15;
		// tries to find the center of the ellipse precisely through finding the
		// root point of limiting lines
		for (int y = center.getYCoord(); y >= center.getYCoord() - regionSize; y--) {
			for (int x = center.getXCoord() - 2; x < center.getXCoord() + 2; x++) {
				if (getImage().getPixel(center.getXCoord(), y).getColor()
						.isComparableTo(lineColor, 60)) {
					this.center = new Point(this.center.getXCoord(), y);
					precisedCenter = true;
					break;
				}
				if (precisedCenter) {
					break;
				}
			}
		}
	}

	/**
	 * <p>
	 * Finds the small radius of the ellipse through going up from the center up
	 * the image until all background has been reached
	 * </p>
	 * */
	private void findSmallRadius() {
		int minWidth = 6;
		findBigRadiusAndCenter();

		this.smallRadius = 0;
		for (int row = center.getYCoord(); row < getImage().getHeight(); row++) {
			int nonConforming = 0;
			for (int col = center.getXCoord() - minWidth / 2; col <= center
					.getXCoord() + minWidth / 2; col++) {
				if (getImage().getPixel(col, row).getColor()
						.isComparableTo(backgroundColor)) {
					nonConforming++;
				}
			}
			if (nonConforming > minWidth / 6) {
				// too much background limit has beeen reached
				this.smallRadius = row - center.getYCoord();
				break;

			}
		}
	}

	/**
	 * Having identified the pie ellipse runs through it and finds the
	 * percentage covered by every color
	 * 
	 * @return A vector where every pair represents the color of the pie slice
	 *         and the precentage it covers
	 */
	public Vector<Pair<Color, Double>> extractPieData() {
		// the analysis is done through browsing the ellipse and up to two
		// colors may appear twice
		HashMap<Color, Double> pieColors = new HashMap<Color, Double>();
		double step = Math.PI / (180 * 10);
		double testRadiusConst = 170.0 / 180;
		for (double alpha = Math.PI; alpha >= -Math.PI; alpha -= step) {
			double startAlpha = alpha;
			int pointY = center.getYCoord()
					+ (int) (Math.sin(alpha) * testRadiusConst * smallRadius);
			int pointX = center.getXCoord()
					+ (int) (Math.cos(alpha) * testRadiusConst * bigRadius);
			Color startPieceColor = getImage().getPixelRegionAverage(
					new Point(pointX, pointY), lineColor);
			Color curPieceColor = startPieceColor;
			while (startPieceColor.isEqualTo(curPieceColor)) {
				alpha -= step;
				if (alpha < -Math.PI) {
					break;
				}
				pointY = center.getYCoord()
						+ (int) (Math.sin(alpha) * (smallRadius * testRadiusConst));
				pointX = center.getXCoord()
						+ (int) (Math.cos(alpha) * (bigRadius * testRadiusConst));
				curPieceColor = getImage().getPixelRegionAverage(
						new Point(pointX, pointY), lineColor);
			}
			if (startAlpha - alpha > step * 7) {
				Double curPercent = ((startAlpha - alpha) / Math.PI) / 2;
				if (!pieColors.containsKey(startPieceColor)) {
					pieColors.put(startPieceColor, curPercent);
				} else {
					Double takenPercent = pieColors.get(startPieceColor);
					pieColors.put(startPieceColor, curPercent + takenPercent);
				}
			}
		}
		Vector<Pair<Color, Double>> result = adjustValues(pieColors);

		return result;
	}

	/**
	 * Compensates line separation line initiated differences
	 * */
	private Vector<Pair<Color, Double>> adjustValues(
			HashMap<Color, Double> pieColors) {
		Vector<Pair<Color, Double>> result = new Vector<Pair<Color, Double>>();
		Iterator it = pieColors.entrySet().iterator();
		double sum = 0.0, toDistribute;
		int count = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			double value = (Double) entry.getValue();
			count++;
			sum += value;
			result.add(new Pair<Color, Double>((Color) entry.getKey(), value));
		}
		toDistribute = (1.0 - sum) / count;
		double adjusted;
		for (int i = 0; i < count; i++) {
			adjusted = result.get(i).getSecond() + toDistribute;
			Double reevaluated = Math.floor((Double) (adjusted) * 100 + 0.5);
			result.get(i).setSecond(reevaluated);
		}
		return result;
	}

}
