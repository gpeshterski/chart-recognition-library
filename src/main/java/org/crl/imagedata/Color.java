package org.crl.imagedata;

import java.util.Vector;

import org.crl.exceptions.ColorNotFoundException;

/**
 * Color is represented as RGB.
 */
public class Color implements Comparable {
	
	/** The  R G B values from 0 to 255 . */
	private int[] components;

	/**
	 * Instantiates a new color from an awtColor
	 */
	public Color(java.awt.Color awtColor) {
		components = new int[3];

		components[0] =  awtColor.getRed();
		components[1] =  awtColor.getGreen();
		components[2] =  awtColor.getBlue();
	}

	/**
	 * @param the color components as R G B values from 0 to 255 
	 */
	public Color(int[] color) {
		components = new int[3];

		try {
			if (color == null)
				throw new ColorNotFoundException(
						ColorNotFoundException.INVALID_COLOR);
		} catch (ColorNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i <= 2; i++) {
			components[i] = validateColorComponent(color[i]);
		}
	}
  
	/**
	 * Validate color component.
	 *
	 * @param color the color to be validated
	 * @return the validated color component
	 */
	private int validateColorComponent(int color) {
		if (color < 0) {
				throw new IllegalStateException("Color component can not be lower than 0");
		}
		if (color > 255) {
			return 255;
		}
		return color;

	}
    
    /**
     * Checks if the color components are close enough to be grey
     *
     * @param hold the maximal deviation between color components
     */
    public boolean isGrey(int hold){
    	if(Math.abs(components[0]-components[1])<hold &&Math.abs(components[1]-components[2])<hold && Math.abs(components[0]-components[2])<hold) 
    		{return true;}
    	return false;
    }
	
	/**
	 * Checks if two colors have equal components
	 */
	public boolean isEqualTo(Color otherColor) {
		for (int i = 0; i < 3; i++) {
			if (components[i] != otherColor.components[i])
				return false;
		}
		return true;
	}

	/**
	 * Calculated through quadratic difference formula More precise than epsilon
	 * difference from any.
	 *
	 * @param otherColor the other color
	 * @return true, if is comparable to
	 */
	public boolean isComparableTo(Color otherColor) {
		int EPSILON = 55;
		return isComparableTo(otherColor, EPSILON);

	}

/**
 * Validates whether two colors are similar whithin a desired EPSILON range of distance.
 *
 * @param otherColor the other color
 * @param EPSILON the epsilon range of similarity between the colors
 * @return true, if is comparable to
 */
	public boolean isComparableTo(Color otherColor, int EPSILON) {
		int difference = 0;
		for (int i = 0; i < 3; i++) {
			difference += calculateQuadriaticDifference(this.components[i],
					otherColor.components[i]);
		}
		if (difference > EPSILON * EPSILON) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "R: " + components[0] + " G:" + components[1] + " B:"
				+ components[2];
	}

	/**
	 * Calculate quadratic difference.
	 *
	 */
	private int calculateQuadriaticDifference(double expectedColor,
			double actualColor) {

		int diff = (int) (actualColor - expectedColor);
		return diff * diff;
	}

	public int hashCode() {
		int multiplier = 255;
		return (int) components[0] * multiplier * multiplier
				+ (int) components[1] * multiplier + (int) components[2];
	}

	public boolean equals(Object obj) {
		Color otherColor = (Color) obj;
		return this.isComparableTo(otherColor, 60);

	}

	public int compareTo(Object other) {
		Color otherColor=(Color)other;
		if(this.components[0]==otherColor.components[0]){
			if(this.components[1]==otherColor.components[1]){
				return ((Integer)this.components[2]).compareTo(otherColor.components[2]);
			}
			return ((Integer)this.components[1]).compareTo(otherColor.components[1]);
		}
		return ((Integer)this.components[0]).compareTo(otherColor.components[0]);
	}
	
	/**
	 * Gets the average of multiple colors.
     */
	public static Color getAverage(Vector<Color> colors){
		Color result=null;
		int red=0,green=0,blue=0;
		if(colors.size()>0){
		for(int i=0;i<colors.size();i++){
			red+=colors.get(i).components[0];
			green+=colors.get(i).components[1];
			blue+=colors.get(i).components[2];
		}
		result=new Color(new int[]{red/colors.size(),green/colors.size(),blue/colors.size()});
		}
		return result;
	}
}
