package org.crl.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.crl.utilities.scaleparsing.DateParser;
import org.crl.utilities.scaleparsing.DateSequenceValidator;
import org.crl.utilities.scaleparsing.fontutils.FontsDAL;
import org.junit.Test;

public class DateValidationTest {
	@Test
	public void tryValidateDates() {
		FontsDAL dal = new FontsDAL("DejaVMUni");
		Vector<Pair<String,Integer>> dateStrings = new Vector<Pair<String,Integer>>();
		dateStrings.add(new Pair<String, Integer>("7/15/13\n\n\n\n 1:20 PM",0));
		dateStrings.add(new Pair<String, Integer>("7/15/13\n\n\n\n 3:21 PM",21));
		dateStrings.add(new Pair<String, Integer>("7/15/13\n\n\n\n 9:22 PM",24));
		DateParser parser = new DateParser(dateStrings, dal.getStringErrors());


DateSequenceValidator validator=new DateSequenceValidator(parser.getDates(), dal.getDigitErrors(), parser.getDateFormat());
Vector<Pair<Comparable,Integer>> res=validator.getValidSequence();
	for (int i = 0; i < res.size(); i++) {
		Calendar p=(Calendar) res.get(i).getFirst();
		System.out.println(p.getTime()+ "   "+ res.get(i).getSecond());
	}
	}
	@Test
	public void tryValidateDates_BadStrings() {
		FontsDAL dal = new FontsDAL("DejaVMUni");
		
		Vector<Pair<String,Integer>> dateStrings = new Vector<Pair<String,Integer>>();
		dateStrings.add(new Pair<String, Integer>("7/15/13\n\n\n\n 1:20 PM",0));
		dateStrings.add(new Pair<String, Integer>("7/15/13\n\n\n\n 3:21 PM",21));
		dateStrings.add(new Pair<String, Integer>("7/15/13\n\n\n\n 9:22 PM",24));
		DateParser parser = new DateParser(dateStrings, dal.getStringErrors());


DateSequenceValidator validator=new DateSequenceValidator(parser.getDates(), dal.getDigitErrors(), parser.getDateFormat());
Vector<Pair<Comparable,Integer>> res=validator.getValidSequence();
	for (int i = 0; i < res.size(); i++) {
		Calendar p=(Calendar) res.get(i).getFirst();
		System.out.println(p.getTime()+ "   "+ res.get(i).getSecond());
	}
	}
}
