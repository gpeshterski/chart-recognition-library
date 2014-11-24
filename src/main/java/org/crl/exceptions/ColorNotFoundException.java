package org.crl.exceptions;

public class ColorNotFoundException extends RuntimeException {
	public static final String NO_LINE_COLOR_FOUND = "No line color was found in the chart.";
	public static final String NO_BAR_COLOR_FOUND = "No bar color was found in the chart.";
    public static final String LINE_COLOR_NOT_FOUND="The expected line color was not found in the chart";
    public static final String INVALID_COLOR="The entered color is not valid.";
    public static final String BACKGROUND_NOT_FOUND="No outer background was identified for the chart.";
    public ColorNotFoundException(String message) {
		super(message);
	}

}
