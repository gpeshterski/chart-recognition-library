package org.crl.utilities;

import java.util.Date;

import org.crl.charts.dataids.DataId;
import org.crl.charts.dataids.Label;
import org.crl.charts.dataids.LegendColor;
import org.crl.charts.dataids.Timestamp;

public class PrettyPrint {
	public static String convert(Pair<DataId, Double> element) {
		Date date = new Date();
		if (element.getFirst() instanceof Timestamp) {
			date.setTime((Long) element.getFirst().getValue());
			return date.toString() + " " + element.getSecond();
		} else {
			double adjustedValue = Math.floor(element.getSecond() + 0.5);

			if (element.getFirst() instanceof LegendColor) {
				return element.getFirst().toString() + " " + adjustedValue+" %";
			} else if (element.getFirst() instanceof Label) {
				return element.getFirst().toString() + " - " + adjustedValue;
			}
		}
		return "";
	}
}
