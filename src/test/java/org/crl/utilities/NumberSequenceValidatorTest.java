package org.crl.utilities;

import static org.junit.Assert.*;

import java.util.Vector;

import org.apache.commons.collections.MultiHashMap;
import org.crl.utilities.scaleparsing.NumberSequenceValidator;
import org.junit.Test;

public class NumberSequenceValidatorTest {

	public void testSequenceValidation() {
		Vector<Pair<Comparable, Integer>> labels = new Vector<Pair<Comparable, Integer>>();
		labels.add(new Pair<Comparable, Integer>(11.0, 0));
		labels.add(new Pair<Comparable, Integer>(17.0, 30));
		labels.add(new Pair<Comparable, Integer>(19.0, 35));
		labels.add(new Pair<Comparable, Integer>(20.0, 45));

		MultiHashMap errorMap = new MultiHashMap();
		for (int i = 0; i < 10; i++) {
			errorMap.put(i, i);
		}
		errorMap.put(3, 1);
		errorMap.put(3, 7);
		errorMap.put(9, 8);
		errorMap.put(7, 8);
		NumberSequenceValidator validator = new NumberSequenceValidator(labels,
				errorMap);
		Vector<Pair<Comparable, Integer>> seq = validator.getValidSequence();
		for (int i = 0; i < seq.size(); i++) {
			System.out.println(seq.get(i).getFirst() + "   "
					+ seq.get(i).getSecond());
		}
	}

}
