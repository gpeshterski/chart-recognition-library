package org.crl.charts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.crl.ChartExtractor;
import org.crl.charts.linechart.LineChart;
import org.crl.charts.linechart.LineChartAxisConfig;
import org.crl.charts.linechart.LineChartConfig;
import org.crl.charts.piechart.PieChartConfig;
import org.crl.imagedata.Color;
import org.crl.imagedata.Image;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.ChartConfigurator;
import org.crl.utilities.Pair;
import org.crl.utilities.ScaleRecognizer;
import org.crl.utilities.scaleparsing.DateParser;
import org.crl.utilities.scaleparsing.DateSequenceValidator;
import org.crl.utilities.scaleparsing.NumberSequenceValidator;
import org.crl.utilities.scaleparsing.fontutils.FontAnalyzer;
import org.crl.utilities.scaleparsing.fontutils.FontsDAL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class LineChartTest {
	public void testTimeStampExtraction_RawClasses() {
		Image image = new Image("src/test/resources/TestCharts/LineCharts/Uncut/im13.png");
		ChartCropper cropper = new ChartCropper(image);
		ChartConfigurator commonConfig = new ChartConfigurator(StandardColors.WHITE, 1);
		ScaleRecognizer recognizer = new ScaleRecognizer(image,cropper.getChartStart(), commonConfig);
		FontsDAL font = new FontsDAL("DejaVMUni");
//		FontsDAL font = new FontsDAL("arial");

		Vector<Pair<String, Integer>> timeTextLabels = recognizer.getTimeLabelStrings();

		DateParser parser = new DateParser(timeTextLabels,font.getStringErrors());
		DateSequenceValidator dateValidator = new DateSequenceValidator(parser.getDates(), font.getDigitErrors(),parser.getDateFormat());
		Vector<Pair<Comparable, Integer>> scale = dateValidator.getValidatedScale(dateValidator.getValidSequence());
		for (int i = 0; i < scale.size(); i++) {
			scale.get(i).setSecond(scale.get(i).getSecond() - cropper.getChartStart().getXCoord());
		}
		XAxisTimeConfiguration xAxis = new XAxisTimeConfiguration(scale);

		Vector<Pair<Comparable, Integer>> scaleLabels = recognizer.getScaleLabels();
		NumberSequenceValidator scaleValidator = new NumberSequenceValidator(scaleLabels, font.getDigitErrors());
		YAxisConfiguration yAxis = new YAxisConfiguration(scaleValidator.getValidatedScale(scaleValidator.getValidSequence()));
		LineChartAxisConfig axisConfig = new LineChartAxisConfig(xAxis, yAxis);
		LineChartConfig chartConfig = new LineChartConfig(axisConfig);
		LineChart chart = new LineChart(cropper.cropAxises(), commonConfig,chartConfig);
		HashMap<Long, Double> data = (HashMap<Long, Double>) chart.extractTimeStampsValuePairs();

		Date d = new Date();

		Iterator it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry el = (Map.Entry) it.next();
			d.setTime((Long) el.getKey());
			System.out.println(d.toString() + " " + el.getValue());
		}
	}

	public void testTimeStampExtraction_WrappedClasses() {
		ChartExtractor extractor = new ChartExtractor("DejaVMUni");
		Image image = new Image(
				"src/test/resources/TestCharts/LineCharts/Uncut/im13.png");
		LineChartConfig config = new LineChartConfig();
		// config.setChartLineColor(new Color(new int[]{ 99,147,198}));

		Vector<Pair<Long, Double>> data = extractor.getLineChartData(image, config);
		for (int i = 0; i < data.size(); i++) {
			Date date = new Date(data.get(i).getFirst());
			System.out.println(date + " " + data.get(i).getSecond());
		}
	}

}
