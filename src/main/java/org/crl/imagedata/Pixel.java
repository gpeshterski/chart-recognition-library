package org.crl.imagedata;

public class Pixel{
	private Point position;
    private Color color;
	public Pixel(Color color, int _xCoord, int _yCoord) {
		this.color=color;
		position = new Point(_xCoord, _yCoord);
	}

	public Color getColor() {
		return color;
	}

	public Point getPosition() {
		return position;
	}
}
