package org.crl.utilities.scaleparsing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crl.utilities.MapUtilities;
import org.crl.utilities.Pair;
/**
 * Used to parse a date string captured from the snapshot, and map it to java date format
 * */
public class DateParser {
	/**
	 * Map with the string errors 
	 * A recognized entry and possible representation
	 * */
	private HashMap<String, List<String>> errorMap;
	private Vector<Pair<String, Integer>> dateStrings;
	private String dateFormat;
	private static final List<String> formats = Arrays.asList("d-MMM","MMM d HH:mm","MMM d YYYY");
	// 15-Jul "d-MMM"
	// Jul 15
	// 21:52 "MMM d HH:mm"
	private static final List<String> ampmFormats = Arrays.asList("MM/dd/yy hh:mm a", "MMM d h:mm a");
	// 7/15/13
	// 9:52 PM "M/dd/yy h:mm a"

	// Jul 15
	// 9:52 PM "MMM d h:mm a"
	private static final String monthNames = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May?|Jun(e)?|Jul(y)?|Aug(ust)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)";
	private static final List<String> formatsPattern = Arrays.asList(monthNames + " (\\d|\\d\\d) (\\d|\\d\\d):(\\d\\d)", "(\\d|\\d\\d)-" + monthNames, monthNames+" (\\d|\\d\\d) (\\d\\d\\d\\d)");
	private static final List<String> ampmFormatsPattern = Arrays.asList("(\\d|\\d\\d)/(\\d|\\d\\d)/(\\d|\\d\\d) (\\d|\\d\\d):(\\d|\\d\\d) (AM|PM)", monthNames	+ " (\\d|\\d\\d) (\\d|\\d\\d):(\\d|\\d\\d) (AM|PM)");

	public DateParser(Vector<Pair<String, Integer>> dateStrings,
			HashMap<String, List<String>> errorMap) {
		this.errorMap = errorMap;
		this.dateStrings = dateStrings;
		this.dateFormat = findDateFormat();
	}

	/**
	 * @return the format of string that dominates the dates
	 * */
	public String getDateFormat() {

		return dateFormat;
	}
    /**
     * @return  pairs of the dates produced from the strings and their positions
     * */
	public Vector<Pair<Comparable, Integer>> getDates() {
		Vector<Pair<Comparable, Integer>> result = new Vector<Pair<Comparable, Integer>>();
		Calendar d = new GregorianCalendar();
		for (int i = 0; i < dateStrings.size(); i++) {
			String dateString=dateStrings.get(i).getFirst();
			if(dateString!=null){
			d = getParsedDate(dateString);
			if (d != null) {
				result.add(new Pair<Comparable, Integer>(d, dateStrings.get(i)
						.getSecond()));
			}
			}
		}
		return result;
	}
	/**
	 * Trims the non-date part of string
	 * 
	 * Example
	 *   Jp 21 Jul !
	 *   
	 *                    ->   21 Jul 2013
	 *   2013
	 * 
	 * */
  private String trimNonDateBulk(String dateString){
	  List<String> formats;
	  //the formats vary on whether the format is 24 or 12 hours
	  if ((dateString.contains("AM") || dateString.contains("PM"))) {
			formats=ampmFormatsPattern;
	  } 
	  else {
			formats=formatsPattern;
		}
	  for(int ind=0;ind<formats.size();ind++){
		Pattern pattern = Pattern
				.compile(formats.get(ind));
		Matcher matcher = pattern.matcher(dateString);
		if(matcher.find()){
	    return (matcher.group());
		}
	  }
		return null;
  }
	/**
	 * Parses enough strings and tries to find the dominating date format
	 * amongst them
	 * */
	private String findDateFormat() {
		String dominating = null;
		int count = 1;
		int firstIndex = 0;
		while (dominating == null && firstIndex<dateStrings.size()-1) {
			dominating = findSingleDateFormat(dateStrings.get(firstIndex).getFirst());
			firstIndex++;
		}
		// a format is dominating if more than half the dates are parsed correctly using it
		for (int j = firstIndex; j < dateStrings.size(); j++) {
			String current = findSingleDateFormat(dateStrings.get(j).getFirst());
			if (!(current == null)) {
				if (!current.contentEquals(dominating)) {
					count--;
					if (count == 0) {
						dominating = current;
					}
				} else {
					count++;
					if (count > dateStrings.size() / 2) {
						return dominating;
					}
				}
			}
		}
		return dominating;
	}
/**
 * Corrects the errors in month names from the string error map
 * */
	private String correctStringErrors(String rawString) {
		Iterator it = errorMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String error = (String) entry.getKey();
			List<String> option = (List<String>) entry.getValue();
			rawString = rawString.replaceAll(error, option.get(0));
		}

		return rawString;
	}
/**
 * Cuts the date string into parts and processes them to be parsable by a java date format matcher
 * */
	private String extractDateParts(String date) {
		String extractedDate="";
		String extractedTime="";
		try{
			date = date.replaceAll("\n{1,}", " ");
			Pattern datePattern = Pattern.compile("\\s*\\d*\\s*\\d*\\s*\\/\\s*\\d*\\s*\\d*\\s*\\/\\s*\\d*\\s*\\d*(?!:)");
			Pattern timePattern = Pattern.compile("([1,2]*\\s*\\d*\\s*[:]*\\s*\\d\\s*\\d\\s*[aApP]\\s*[mM]|[1,0]*\\s*\\d*\\s*[:]*\\s*\\d*\\s*\\d*)");
			Matcher dateMatcher = datePattern.matcher(date);
			if(dateMatcher.find()){
				extractedDate = dateMatcher.group().replaceAll(" ", "");
				Matcher timeMatcher = timePattern.matcher(date.substring(dateMatcher.group().length(),date.length()));
				if(timeMatcher.find()){
					extractedTime = timeMatcher.group().replaceAll(" ", "");
					if(!extractedTime.contains(":") || extractedTime.toUpperCase().contains("M")){
						extractedTime = extractedTime.substring(0, extractedTime.length()-2)+" "+extractedTime.substring(extractedTime.length()-2, extractedTime.length());
					}else {
						extractedTime = extractedTime.substring(0, extractedTime.length()-2)+" "+extractedTime.substring(extractedTime.length()-2, extractedTime.length());
					}
				}
			}
			}catch (Exception e) {
				// unable to parse date
				return null;
			}
			String dateLastPart = extractedDate.substring(extractedDate.lastIndexOf("/")+1, extractedDate.length());
			String timeFirstPart = extractedTime.substring(0, extractedTime.indexOf(":"));
			if(dateLastPart.length()+timeFirstPart.length()==4 && dateLastPart.length()!=2 && timeFirstPart.length()!=2){
				extractedTime = extractedDate.substring(extractedDate.length()-1)+extractedTime;
				extractedDate = extractedDate.substring(0, extractedDate.length()-1);
			}
			return extractedDate+" "+extractedTime;
		

		
		/*
String partsCompiled=stringResult.toString().trim();
		String trimmedBulk= trimNonDateBulk(partsCompiled);
		if(!(trimmedBulk==null)){
			return trimmedBulk.trim();
		}
		return null;
		*/
	}

	public Calendar getDate(String dateStringRaw) {
		Calendar result = getParsedDate(dateStringRaw);
		/*
		 * if (result != null) { return result.getTime(); }
		 */
		return result;
	}
/**
 * Receives a raw string,trims it and returns a date parsed with the accepted format
 * */
	private Calendar getParsedDate(String dateStringRaw) {
		String dateString = extractDateParts(dateStringRaw);
		if(dateString!=null){
		Calendar dateResult = Calendar.getInstance();
		dateResult = parseDate(dateString, dateFormat);
		return dateResult;
		}
		return null;
	}

	/**
	 * @return the recognized date format of the string null - was not
	 *         recognized as date
	 * */
	private String findSingleDateFormat(String dateStringRaw) {
		dateStringRaw = correctStringErrors(dateStringRaw);
		String dateString = extractDateParts(dateStringRaw);
		if(dateString!=null){
		String dateFormat = null;
		if ((dateString.endsWith("AM") || dateString.endsWith("PM"))) {
			dateFormat = findDateFormatFromMap(dateString, ampmFormats);
		} else {
			dateFormat = findDateFormatFromMap(dateString, formats);
		}
		return dateFormat;
		}
		return null;
	}

	/**
	 * @return the recognized date format (with defined date type) of the string
	 *         null was not recognized as date
	 * */
	private String findDateFormatFromMap(String dateString, List<String> formats) {
		Calendar dateResult = null;
		for (String format : formats) {
			dateResult = parseDate(dateString, format);
			if (dateResult != null) {
				return format;
			}
		}
		return null;
	}
/**
 * Tries to parse the date string with the given java date format
 * @return null if the parsing failed
 * */
	private Calendar parseDate(String dateString, String format) {
		Date dateResult = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			dateResult = formatter.parse(dateString);
		} catch (ParseException ex) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateResult);
		return cal;
	}

}
