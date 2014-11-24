package org.crl.utilities.scaleparsing.fontutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiHashMap;
import org.crl.utilities.MapUtilities;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
/**
 * Used to process use of fonts in error dictionaries
 * Providing access to the files where font errors are stored
 * */
public class FontsDAL {

	private String fontName;
	FontErrors errors;
	private static final String DIGIT_ERRORS_STRING = "digitErrors";
	private static final String STRING_ERRORS_STRING = "stringErrors";
	private static final String DEFAULT_FONTS_DIRECTORY= FONT_DIR = "src" + File.separator + "main"
			+ File.separator + "resources" + File.separator + "FontBase";
	private static String FONT_DIR = "src" + File.separator + "main"
			+ File.separator + "resources" + File.separator + "FontBase";

	/**
	 * In case the font is not present in the default fonts directory 
	 * a creation console tool will be invoked
	 * @param fontName the font name not case sensitive
	 */
	public FontsDAL(String fontName) {
		this.fontName = fontName.toLowerCase();
		this.FONT_DIR=DEFAULT_FONTS_DIRECTORY;
		if (!readFont()) {
			createFont();
		}

	}
/**
 * Use this to change the directory where the font errors are stored
 * */
	public void setFontDir(String fontDir) {
		this.FONT_DIR = fontDir;
	}

	public HashMap<String, List<String>> getDigitErrors() {
		return errors.getDigitErrors();
	}

	public HashMap<String, List<String>> getStringErrors() {
		return errors.getStringErrors();
	}
/**
 * Reads the font errors from its dedicated file
 * */
	private boolean readFont() {
		// finds the file
		// parses and sets to the fields
		String fileName = FONT_DIR + File.separator + fontName + ".json";
		File file = new File(fileName);
		if (!file.exists()) {
			return false;
		}
		FileReader fileReader;
		try {
			fileReader = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fileReader);
			this.errors = FontErrors.toObject(reader);
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	private void createFont() {
		FontAnalyzer analyzer = new FontAnalyzer(fontName);
		MultiHashMap stringErrors = analyzer.getErrors("String recognition");
		MultiHashMap digitErrors = analyzer.getErrors("Digit recognition");

		this.errors = new FontErrors(stringErrors, digitErrors);
		writeToResource(errors.toString());
	}

	public void writeToResource(String fontResult) {

		String fileName = FONT_DIR + File.separator + fontName + ".json";

		File file = new File(FONT_DIR);
		file.mkdirs();
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName);
			writer.write(fontResult);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				writer.close();
			} catch (IOException writerClosed) {
                throw new IllegalStateException(writerClosed);
			}
		}
	}
}
