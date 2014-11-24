package org.crl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.crl.imagedata.Color;
import org.crl.utilities.Pair;

/**
 * Provides a default implementation for comparing with backend data and set min
 * acceptable percent match.
 */
public class DataMatcher {

	/**
	 * Checks if line chart is similar. Finds the closest time stamp to compare
	 * with original data, in case the stamps are too far this is a 0% match
	 * 
	 * @param actualData
	 *            the actual data
	 * @param expectedData
	 *            the expected data
	 * @param minSimilarity
	 *            in percent
	 * @return true, if is line chart similar
	 */
	public static boolean isLineChartSimilar(
			Vector<Pair<Long, Double>> actualData,
			Vector<Pair<Long, Double>> expectedData, int minSimilarity) {
		sortByTimestamp(actualData);
		sortByTimestamp(expectedData);
		if (expectedData.size() > 0) {
			Long timeInterval = expectedData.get(expectedData.size() - 1)
					.getFirst() - expectedData.get(0).getFirst();
			Long maxTimeDifference = timeInterval / expectedData.size();
			Double differenceSum = 0.0;
			int actIndex = 0;
			for (int expIndex = 0; expIndex < expectedData.size() - 1; expIndex++) {
				Long curExpectedDataStamp = expectedData.get(expIndex)
						.getFirst();
				while (actualData.get(actIndex).getFirst() <= curExpectedDataStamp) {
					actIndex++;
					// we are out of the actual data array
					if (actIndex >= actualData.size()) {
						break;
					}
				}
				int closerTimeStampIndex;
				// selects the closer point
				if ((curExpectedDataStamp - actualData.get(actIndex - 1)
						.getFirst()) < (actualData.get(actIndex).getFirst() - curExpectedDataStamp)) {
					closerTimeStampIndex = actIndex - 1;
				} else {
					closerTimeStampIndex = actIndex;
				}

				// tests if point closest is reasonably far
				if (Math.abs(actualData.get(actIndex - 1).getFirst()
						- curExpectedDataStamp) <= maxTimeDifference) {
					// calculates value difference
					double expValue = expectedData.get(expIndex).getSecond();
					double actValue = actualData.get(closerTimeStampIndex)
							.getSecond();
					double difference = getDifference(actValue, expValue);
					differenceSum += difference;
				} else {
					// data is too far, no similarity
				}
			}

			double difference = differenceSum / expectedData.size();
			return isSimilar(difference, minSimilarity);
		}
		return false;
	}

	/**
	 * Checks if is pie chart similar.
	 * 
	 * @param actualData
	 *            the actual data
	 * @param expectedData
	 *            the expected data
	 * @param minSimilarity
	 *            in percent
	 * @return true, if is pie chart similar
	 */
	public static boolean isPieChartSimilar(
			Vector<Pair<Color, Double>> actualData,
			Vector<Pair<Color, Double>> expectedData, int minSimilarity) {
		sortByColor(actualData);
		sortByColor(expectedData);
		if (expectedData.size() > 0) {
			double difference = 0;
			for (int expIndex = 0; expIndex < expectedData.size(); expIndex++) {
				if (expectedData.get(expIndex).getFirst()
						.isComparableTo(actualData.get(expIndex).getFirst())) {
					/* the colors discovered are alike */
					difference += Math
							.abs(expectedData.get(expIndex).getSecond()
									- actualData.get(expIndex).getSecond());
				} else if (expectedData.get(expIndex).getSecond() > 4.0) {
					/*
					 * A color int the expected data was not found in the actual
					 */
					return false;
				}
			}
			return isSimilar(difference, minSimilarity);
		}
		return false;
	}

	/**
	 * Checks if is bar chart similar.
	 * 
	 * @param actualData
	 *            the actual data
	 * @param expectedData
	 *            the expected data
	 * @param minSimilarity
	 *            in percent
	 * @return true, if is bar chart similar
	 */
	public static boolean isBarChartSimilar(
			Vector<Pair<String, Double>> actualData,
			Vector<Pair<String, Double>> expectedData, int minSimilarity) {
		if (expectedData.size() != actualData.size()) {
			return false;
		}
		if (expectedData.size() > 0) {
			double differenceSum = 0.0;
			for (int i = 0; i < expectedData.size(); i++) {
				double expValue = expectedData.get(i).getSecond();
				double actValue = actualData.get(i).getSecond();

				double difference = getDifference(actValue, expValue);
				differenceSum += difference;
			}
			differenceSum /= expectedData.size();
			return isSimilar(differenceSum, minSimilarity);
		}
		return false;
	}

	/**
	 * Sort by timestamp.
	 * 
	 * @param values
	 *            the values
	 * @return the vector
	 */
	private static Vector<Pair<Long, Double>> sortByTimestamp(
			Vector<Pair<Long, Double>> values) {
		Collections.sort(values, new Comparator<Pair<Long, Double>>() {
			public int compare(Pair<Long, Double> o1, Pair<Long, Double> o2) {
				if (o1.getFirst().equals(o2.getFirst())) {
					return o1.getSecond().compareTo(o2.getSecond());
				}
				return o1.getFirst().compareTo(o2.getFirst());
			}
		});
		return values;
	}

	/**
	 * Sort by color.
	 * 
	 * @param values
	 *            the values
	 * @return the vector
	 */
	private static Vector<Pair<Color, Double>> sortByColor(
			Vector<Pair<Color, Double>> values) {
		Collections.sort(values, new Comparator<Pair<Color, Double>>() {
			public int compare(Pair<Color, Double> o1, Pair<Color, Double> o2) {
				if (o1.getFirst().equals(o2.getFirst())) {
					return o1.getSecond().compareTo(o2.getSecond());
				}
				return o1.getFirst().compareTo(o2.getFirst());
			}
		});
		return values;
	}

	/**
	 * Gets the difference.
	 * 
	 * @param actValue
	 *            the actual value
	 * @param expValue
	 *            the expected value
	 * @return the difference
	 */
	private static double getDifference(double actValue, double expValue) {
		double difference = Math.abs(expValue - actValue);
		if (Math.abs(Math.max(expValue, actValue)) > 0.1) {
			// when 0 is compared to 0.01 difference would be 100%
			difference /= Math.abs(Math.max(expValue, actValue));
		} else {
			difference = 0.0;
		}
		return difference;
	}

	private static boolean isSimilar(double differenceSum, int minSimilarity) {
		if (differenceSum < ((100 - minSimilarity) / 100.0)) {
			return true;
		}
		return false;
	}

}
