package org.crl.exceptions;

public class NoDataException extends Exception{
	public NoDataException(String message) {
		super(message);
	}
	/**
	 * No data available in the chart
	 * */
	public NoDataException() {
		super("No data available in the chart");
	}
}
