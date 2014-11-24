package org.crl.charts;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.crl.imagedata.Point;
import org.crl.utilities.Pair;
/**
 * Stores the time values at the axis and their position on 
 * the cropped line chart image
 * */
public class XAxisTimeConfiguration {
	private Vector<Pair<Comparable, Integer>> timeLabels;

	public XAxisTimeConfiguration(Vector<Pair<Comparable, Integer>> timeLabels) {
		this.timeLabels = timeLabels;
	}
/**
 * @param xCoord of a point on the image
 * @return the time stamp corresponding
 * */
	public Long getTimePerTimestamp(int xCoord) {
		Integer timeLow = timeLabels.firstElement().getSecond();
		Long timeLowValue = ((Calendar)timeLabels.firstElement().getFirst()).getTimeInMillis();
		Long interval = getMillisPerPixelLinear() * (xCoord - timeLow);
		Long result = timeLowValue + interval;
		return result;
	}
/**
 * calculates the time to pixels relation
 * */
	private Long getMillisPerPixelLinear() {
		Long result;
		Long timeHighValue = ((Calendar) timeLabels.lastElement().getFirst()).getTimeInMillis();
		Long timeLowValue = ((Calendar)timeLabels.firstElement().getFirst()).getTimeInMillis();
		Integer timeHigh = timeLabels.lastElement().getSecond();
		Integer timeLow = timeLabels.firstElement().getSecond();
		Long totalInterval = timeHighValue - timeLowValue;

		result = totalInterval / (timeHigh - timeLow);

		return result;
	}

}
