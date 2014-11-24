package org.crl.charts.dataids;

/**
 * Timestamp for line chart data.
 */
public class Timestamp implements DataId {
	
	/** The timestamp. */
	private Long timestamp;

	public Object getValue() {
		return timestamp;
	}

	public String toString() {
		return timestamp.toString();
	}
	public Timestamp(Long value) {
		this.timestamp = value;
	}
}
