package org.crl.utilities.scaleparsing.fontutils;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.MultiHashMap;

import com.google.gson.Gson;
/**
 * Generates the Json serialization for string and digit errors 
 * */
public class FontErrors {
	private HashMap<String, List<String>> stringErrors;
	private HashMap<String, List<String>> digitErrors;

	public FontErrors(HashMap<String, List<String>> stringErrors, HashMap<String, List<String>> digitErrors) {
		this.stringErrors = stringErrors;
		this.digitErrors = digitErrors;
	}

	public String toString() {

		Gson gson = new Gson();
		String data = gson.toJson(this);
		return data;
	}

	public HashMap<String, List<String>> getStringErrors() {
		return stringErrors;
	}

	public HashMap<String, List<String>> getDigitErrors() {
		return digitErrors;
	}

	public static FontErrors toObject(BufferedReader reader) {
		Gson gson = new Gson();
		FontErrors result = gson.fromJson(reader, FontErrors.class);
		return result;
	}
}
