package org.crl.imagedata;

public class Point {
	private int xCoord;
	private int yCoord;

	public Point(int xCoord, int yCoord) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}

	public int getXCoord() {
		return xCoord;
	}

	public int getYCoord() {
		return yCoord;
	}

	@Override
	public String toString() {
		String result;
		result = "Point: X:" + xCoord + " Y:" + yCoord;
		return result;

	}

	/*
	 * Change is used to find the position of a neighbor point
	 * 
	 */
	public Point move(Point change) {
		Point resultPoint = new Point(this.xCoord + change.xCoord, this.yCoord
				+ change.yCoord);
		return resultPoint;
	}

	@Override
	public int hashCode() {
		// large enough to guarantee that no two points will share the same hash
		// code
		final int PRIME = 7000003;
		return xCoord * PRIME + yCoord;

	}

	@Override
	public boolean equals(Object arg0) {
		Point other = (Point) arg0;
		return this.xCoord == other.xCoord && this.yCoord == other.yCoord;
	}
}
