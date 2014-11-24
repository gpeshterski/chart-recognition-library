package org.crl.exceptions;

public class DataExtractionException extends RuntimeException {
	private final static String DEFAULT_MESSAGE="Failed to extract data from image";
	public DataExtractionException(String message) {
		super(message);
	}
	
	public DataExtractionException(String message, Throwable cause) {
		super(message, cause);
	}
	public DataExtractionException(Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}
	public DataExtractionException() {
		super(DEFAULT_MESSAGE);
	}
}
