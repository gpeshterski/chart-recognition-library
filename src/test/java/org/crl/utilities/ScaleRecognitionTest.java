package org.crl.utilities;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Vector;

import org.crl.LibraryLoaderSingleton;
import org.crl.charts.ChartCropper;
import org.crl.imagedata.Image;
import org.crl.imagedata.Point;
import org.crl.imagedata.StandardColors;
import org.crl.utilities.scaleparsing.NumberSequenceValidator;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class ScaleRecognitionTest {
	/*
	 * @Test public void testNumberLabelReading() { Point p = new Point(260,
	 * 290); Image image = new
	 * Image("src/main/resources/ImageData/NumberLabel.png"); ScaleRecognizer
	 * recognizer = new ScaleRecognizer(image, new Point(0, 0)); String actual =
	 * recognizer.readLeftText(p); int expected = 1234; assertEquals(expected,
	 * Integer.parseInt(actual.trim())); }
	 * 
	 * @Test public void testDateLabelReading() { Point p = new Point(40, 30);
	 * Image image = new Image("src/main/resources/ImageData/DateLabel.png");
	 * ScaleRecognizer recognizer = new ScaleRecognizer(image, new Point(0, 0));
	 * String actual = recognizer.readLowerText(p); String expected =
	 * "Jul 17\n10:27 PM"; assertEquals(expected, actual.trim()); }
	 */
	/*
	 * @Test public void testScaleRecognition() { // Image image = new //
	 * Image("src/main/resources/ImageData/DateLabel.png"); Image image = new
	 * Image( "src/main/resources/TestCharts/LineCharts/Uncut/cpu_usage.png");
	 * ChartConfiguration chartConfig = new ChartConfiguration(
	 * StandardColors.WHITE, 1); ChartCropper cropper = new ChartCropper(image);
	 * ScaleRecognizer scale = new ScaleRecognizer(image,
	 * cropper.findChartStart(), chartConfig); scale.findScaleLabels();
	 * scale.findTimeLabels(); }
	 * 
	 * @Test public void testTextRecognition() { //
	 * System.out.println(OCRReader.recognizeText(new //
	 * Image("src/main/resources/ImageData/outLeft.png"))); }
	 * 
	 * @Test public void testDuplicatedImage() { Image im = new
	 * Image("src/main/resources/ImageData/outLeft.png"); Image duplicated =
	 * im.duplicate(); // duplicated.saveAsFile("c:\\duplicated.png", "png"); //
	 * System.out.println(OCRReader.recognizeText(duplicated)); }
	
	@SuppressWarnings("rawtypes")
	@Test
	public void tryRecognizeYScale() {
		for (int j = 1; j <= 4; j++) {
			System.out.println("Test case: " + j);
			Image image = new Image("src/test/resources/" + j + ".png");
			ChartCropper cropper = new ChartCropper(image);
			ChartConfigurator config = new ChartConfigurator(
					StandardColors.WHITE, 1);
			ScaleRecognizer recognizer = new ScaleRecognizer(image,
					cropper.getChartStart(), config);
			Vector<Pair<Comparable, Integer>> res = recognizer.getScaleLabels();
			NumberSequenceValidator validator = new NumberSequenceValidator(
					res, null);
			Vector<Pair<Comparable, Integer>> result = validator
					.getValidSequence();

			for (int i = 0; i < result.size(); i++) {
				System.out.println(result.get(i).getFirst());
			}

		}
	}
/*
	@Test
	public void tryRecognizeXScale() {
		Image image = new Image("src/test/resources/" + 1 + ".png");
		ChartCropper cropper = new ChartCropper(image);
		ChartConfigurator config = new ChartConfigurator(StandardColors.WHITE,
				1);
		ScaleRecognizer recognizer = new ScaleRecognizer(image,
				cropper.getChartStart(), config);
		Vector<Pair<Comparable, Integer>> timeLabels = recognizer
				.getTimeLabels();
		System.out.println(timeLabels.size());
	System.out.println(timeLabels.get(0).getFirst()+"   "+timeLabels.get(1));
	}
*/
}
