package org.crl.utilities.scaleparsing;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.crl.utilities.Pair;

public abstract class SequenceValidator {
	private Vector<Pair<Comparable, Integer>> labelSequence;

	private HashMap<String, List<String>> digitErrorMap;

	/**
	 * @param labels
	 *            recognized by the OCR
	 * @param map
	 *            of possible errors represented as possible actual values for a
	 *            recognized digits
	 */
	public SequenceValidator(Vector<Pair<Comparable, Integer>> labels,
			HashMap<String, List<String>> digitErrorMap) {
		this.labelSequence = sortBYScraper(labels);
		this.digitErrorMap = digitErrorMap;
	}

	/**
	 * Sorts the labels by scraper position ascending order
	 */
	private static Vector<Pair<Comparable, Integer>> sortBYScraper(
			Vector<Pair<Comparable, Integer>> labels) {
		Collections.sort(labels, new Comparator<Pair<Comparable, Integer>>() {
			public int compare(Pair<Comparable, Integer> o1,
					Pair<Comparable, Integer> o2) {
				if (o1.getSecond().equals(o2.getSecond())) {
					return o1.getFirst().compareTo(o2.getFirst());
				}
				return o1.getSecond().compareTo(o2.getSecond());
			}
		});
		return labels;
	}

	public Vector<Pair<Comparable, Integer>> getLabelSequence() {
		return labelSequence;
	}

	public HashMap<String, List<String>> getErrorMap() {
		return digitErrorMap;
	}

	protected boolean isEPSEqual(double valueA, double valueB) {
		return Math.abs(valueA - valueB) <= (Math.max(
				Math.min(valueA, valueB) / 10.0, 0.001));
	}
/**
 * Tries to generate a valid sequence
 * In case no satisfying sequence exists, returns the most likely one
 * */
	public Vector<Pair<Comparable, Integer>> getValidSequence() {
		Vector<Pair<Comparable, Integer>> sequence = trySequence(
				getLabelSequence(), 0);
		if (sequence != null) {
			return sequence;
		}
		return getLabelSequence();// might be wrong
	}

	protected abstract Vector<Pair<Comparable, Integer>> trySequence(
			final Vector<Pair<Comparable, Integer>> sequence, int position);

	public Vector<Pair<Comparable, Integer>> getValidatedScale(
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
	 * Validates whether the sequence is increasing and the number intervals
	 * represent equivalent pixel intervals
	 */
	protected boolean isValidSequence(Vector<Pair<Comparable, Integer>> labels) {
		if (labels.get(0).getFirst().compareTo(labels.get(1).getFirst()) > 0) {
			return false;
		}

		double curInterval = getValueInterval(labels.get(1).getSecond(), labels
				.get(0).getSecond(),  labels.get(1).getFirst(),
				 labels.get(0).getFirst());

		double prevInterval;

		for (int i = 2; i < labels.size(); i++) {
			if ( labels.get(i).getFirst().compareTo(labels.get(i - 1)
					.getFirst())<0) {
				return false;
			}
			prevInterval = curInterval;
			curInterval = getValueInterval(labels.get(i).getSecond(), labels
					.get(i - 1).getSecond(), labels.get(i).getFirst(),
					 labels.get(i - 1).getFirst());
			if (!isEPSEqual(prevInterval, curInterval)) {
				return false;
			}
		}
		return true;
	}
/**
 * A is the larger, B is the smaller
 * */
	protected abstract double getValueInterval(int pixelA, int pixelB, Object valueA,
			Object valueB);
	
	
	
}
