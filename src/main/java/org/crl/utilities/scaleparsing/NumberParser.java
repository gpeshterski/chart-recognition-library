package org.crl.utilities.scaleparsing;

import java.util.Vector;
/** 
 * To improve recognition rate of digits the cropped number image is 
 * replicated a number of times and then actual value is extracted*/
public class NumberParser {
	private String string;
	private int multi;

	/**
	 * @param String
	 *            and the duplication count
	 * */
	public NumberParser(String string, int multi) {
		this.string = string;
		this.multi = multi;
	}
/**
 * Calculates the number of meaqningful signs in the number
 * <br>
 * 3.1573.157 3.157    
 * <br>
 * multi=3   5 meaningful digits 
 * */
	private int getNumberDigitsCount() {

		int numSymbolsCount = 0;
		for (int i = 0; i < string.length(); i++) {
			char sign = string.charAt(i);
			if (sign == '-' || sign == '.' || (sign >= '0' && sign <= '9')) {
				numSymbolsCount++;
			}
		}
		// / it is most likely that all characters are available at the
		// beginning of the string
		int symbolsInNum = (numSymbolsCount + multi - 1) / multi;

		return symbolsInNum;
	}
/**
 * Extracts the actual number string
 * */
	private String getNumberString() {
		int digitsCount = getNumberDigitsCount();
		StringBuilder numberString=new StringBuilder();
		int num = 0;
		for (int i = 0; i < string.length(); i++) {
			char sign = string.charAt(i);
			if (sign == '-' || sign == '.' || (sign >= '0' && sign <= '9')) {
				num++;
				numberString.append(sign);
				if (num == digitsCount) {
					return numberString.toString();
				}
			}
		}
		return null;
	}

	public Double getNumber() {
		double result;
		try{
		 result= Double.parseDouble(getNumberString());
		}
		catch(Exception ex){
			return Double.MIN_VALUE;
		}
		return result;
	}
}
