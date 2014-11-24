package org.crl.utilities;

import static org.junit.Assert.*;

import java.util.Vector;

import org.crl.DataMatcher;
import org.junit.Test;

public class DataMatcherTest {

	@Test
	public void test() {
		Vector<Pair<Long, Double>> expected = new Vector<Pair<Long, Double>>();
		Vector<Pair<Long, Double>> actual = new Vector<Pair<Long, Double>>();
		expected.add(new Pair<Long, Double>(1l, 1.0));
		expected.add(new Pair<Long, Double>(2l, 1.0));
		expected.add(new Pair<Long, Double>(3l, 1.5));
		expected.add(new Pair<Long, Double>(4l, 1.75));
		expected.add(new Pair<Long, Double>(5l, 2.0));
		actual.add(new Pair<Long, Double>(1l, 1.0));
		actual.add(new Pair<Long, Double>(2l, 1.75));
		actual.add(new Pair<Long, Double>(3l, 2.0));
		actual.add(new Pair<Long, Double>(4l, 1.5));
		actual.add(new Pair<Long, Double>(5l, 1.0));
		System.out.println(DataMatcher.isLineChartSimilar(actual, expected,
				85));
	}

}
