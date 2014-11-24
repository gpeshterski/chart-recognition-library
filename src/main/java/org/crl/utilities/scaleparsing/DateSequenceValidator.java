package org.crl.utilities.scaleparsing;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.text.Utilities;

import org.crl.utilities.Pair;

/**
 * The dates and their position on the axis have been extracted
 * With the dates that appear correct they need to be fitted to the sequence positioning 
 * Generates a valid sequence from a vector of dates on a scale
 * */
public class DateSequenceValidator extends NumberSequenceValidator {
	private String dateFormat;

	/**
	 * @param labels
	 *            the extracted plausible date labels and their positions
	 * */
	public DateSequenceValidator(Vector<Pair<Comparable, Integer>> labels,
			HashMap<String, List<String>> digitErrorMap, String dateFormat) {
		super(labels, digitErrorMap);
		this.dateFormat = dateFormat;
	}
  /**
   * Recursively tries the alternative representations of every date 
   * in the sequence and generates a valid one
   * */
	@Override
	protected Vector<Pair<Comparable, Integer>> trySequence(
			Vector<Pair<Comparable, Integer>> sequence, int position) {
		if (isValidSequence(sequence)) {
			return sequence;
		}
		if (position >= sequence.size()) {
			return null;
		}
		Vector<Pair<Comparable, Integer>> modifiedSequence = sequence;
		Pair<Comparable, Integer> current = sequence.get(position);

		// generates all options for the number and tries them
		Vector<Calendar> numberVariants = getDateVariants((Calendar) current
				.getFirst());

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
 * Calculates the time interval equivalent to a pixel in the scale
 * */
	private Long getValueInterval(int pixelA, int pixelB, Date valueA,
			Date valueB) {
		Long a = ((Date) valueA).getTime();
		Long b = ((Date) valueA).getTime();
		return ((pixelA - pixelB) / (a - b));
	}

	/**
	 * Parses the date in its components and tries to find out the correct date
	 * */
	private Vector<Calendar> getDateVariants(Calendar date) {
		double d = date.get(Calendar.DATE);
		Vector<Double> dateOptions = getNumberVariants(d);
		double h = date.get(Calendar.HOUR);
		Vector<Double> hourOptions = getNumberVariants(h);

		Vector<Calendar> dateVariants = new Vector<Calendar>();
		for (int i = 0; i < dateOptions.size(); i++) {
			for (int j = 0; j < hourOptions.size(); j++) {
				Calendar elem = Calendar.getInstance();
				elem.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
						dateOptions.get(i).intValue(), hourOptions.get(j)
								.intValue(), date.get(Calendar.MINUTE));
				dateVariants.add(elem);
			}
		}
		if (!dateFormat.contains("MMM")) {
			// month was number in the string - example 23.07.2013
			double m = date.get(Calendar.MONTH);
			Vector<Double> monthOptions = getNumberVariants(m);
			for (int i = 0; i < dateVariants.size(); i++) {
				for (int j = 0; j < monthOptions.size(); j++)
					if (date.get(Calendar.MONTH) != monthOptions.get(j)) {

					}
			}
		}
		return dateVariants;
	}

	@Override
	/**
	 * Calculates the equivalence between pixels and time on the scale
	 * */
	protected double getValueInterval(int pixelA, int pixelB, Object valueA,
			Object valueB) {
		Calendar valA = (Calendar) valueA;
		Calendar valB = (Calendar) valueB;
		Double interval = (double) (pixelA - pixelB);
		return interval
				/ ((valA.getTimeInMillis() - valB.getTimeInMillis()) / (60 * 1000));
	}

}
