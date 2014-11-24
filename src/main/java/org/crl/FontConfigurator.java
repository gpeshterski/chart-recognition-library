package org.crl;

import java.util.Scanner;

import org.crl.utilities.scaleparsing.fontutils.FontsDAL;
/**
 * Use this to add new fonts to the font base
 * 
 * */
public class FontConfigurator {

	public static void configureFont(){
		System.out.println("Font library addition tool:");
		System.out.print("Please enter font name:");
		Scanner input = new Scanner(System.in);
		String fontName = input.nextLine();
		FontsDAL font = new FontsDAL(fontName);
	}

}
