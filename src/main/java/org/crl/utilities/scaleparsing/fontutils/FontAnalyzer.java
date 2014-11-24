package org.crl.utilities.scaleparsing.fontutils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.collections.MultiHashMap;
import org.crl.imagedata.Image;
import org.crl.utilities.OCRReader;
/**
 * Tool for entering error data for the fonts recognition
 * Symbols recognized may appear in multiple expected value pairs
 * 
 * */
public class FontAnalyzer {
	String fontName;
	private final List<String> RENDERABLE_WORDS = new LinkedList<String>();

	public FontAnalyzer(String fontName) {
		this.fontName = fontName;
	}
	public MultiHashMap getErrors(String message) {
		MultiHashMap result = new MultiHashMap();
		System.out.println("OCR correction tool press >exit to end");
		System.out.println("Symbols recognized may appear in multiple expected value pairs");
		System.out.println(message);
		Scanner input = new Scanner(System.in);
		do {
			System.out.println("Symbols recognized:");
	
			String actual = input.nextLine();
			if (actual.contains(">exit")) {
				break;
			}
			System.out.println("Expected value  :");
			String expected = input.nextLine();
			result.put(actual, expected);
		} while (true);

		return result;
	}
}
