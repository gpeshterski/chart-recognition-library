package org.crl.utilities.scaleparsing;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.collections.MultiHashMap;
import org.crl.utilities.Pair;

/**
 * Tries to generate a valid number sequence of the scrapers based on possible
 * digit misrecognition, using the scale 
 * */
public class NumberSequenceValidator extends SequenceValidator {

	public NumberSequenceValidator(Vector<Pair<Comparable, Integer>> labels,
			HashMap<String, List<String>> digitErrorMap) {
		super(labels, digitErrorMap);
	}

	/**
	 * Checks whether the generated sequence conforms to the ascendency rule
	 * */
	protected Vector<Pair<Comparable, Integer>> trySequence(
			final Vector<Pair<Comparable, Integer>> sequence, int position) {
		if (isValidSequence(sequence)) {
			return sequence;
		}
		if (position >= sequence.size()) {
			return null;
		}
		Vector<Pair<Comparable, Integer>> modifiedSequence = sequence;
		Pair<Comparable, Integer> current = sequence.get(position);
		// generates all options for the number and tries them
		Vector<Double> numberVariants = getNumberVariants((Double) current
				.getFirst());
		// modifies the next element of the sequence
		for (int i = 0; i < numberVariants.size(); i++) {
			current.setFirst(numberVariants.get(i));
			modifiedSequence.set(position, current);
			Vector<Pair<Comparable, Integer>> result = trySequence(
					modifiedSequence, position + 1);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Uses a recursion to generate possible actual values of the recognized
	 * number
	 * */
	private void generateNumberVariant(Vector<Double> variants, int currentNum,
			int digitPos, Vector<Integer> digits) {
		if (digitPos >= digits.size() - 1) {
			variants.add((double) currentNum);
			return;
		}
		int nextDigit = digits.get(digitPos + 1);
		List<String> options = (List<String>) getErrorMap().get(
				((Integer) nextDigit).toString());
		if(options == null){
			return;
		}
		// tries the options for every digit
		for (int j = 0; j < options.size(); j++) {
			int numGenerated = currentNum * 10
					+ Integer.parseInt(options.get(j));
			generateNumberVariant(variants, numGenerated, digitPos + 1, digits);
		}
	}

	/**
	 * Generates the recognition variants for the number through 
	 * taking every digit and generating the variants for it based on the errors map
	 * */
	protected Vector<Double> getNumberVariants(Double number) {
		Integer wholePart = number.intValue();
		double afterDot = number - wholePart;
		// the fraction is not considered important enough to try variants for
		// it
		Vector<Integer> digits = new Vector<Integer>();
		while (wholePart > 0) {
			digits.add(wholePart % 10);
			wholePart /= 10;
		}
		for (int i = 0; i < digits.size() / 2; i++) {
			int current = digits.get(i);
			digits.set(i, digits.get(digits.size() - i - 1));
			digits.set(digits.size() - i - 1, current);
		}
		Vector<Double> numberVariants = new Vector<Double>();
		generateNumberVariant(numberVariants, 0, -1, digits);
		for (int i = 0; i < numberVariants.size(); i++) {
			numberVariants.set(i, numberVariants.elementAt(i) + afterDot);
		}
		return numberVariants;
	}

	@Override
	/**
	 * Calculates the actual value interval corresponding to the pixels interval
	 * */
	protected double getValueInterval(int pixelA, int pixelB, Object valueA,
			Object valueB) {
		return (Double) ((pixelA - pixelB) / Math.abs((Double) valueA
				- (Double) valueB));
	}

}
