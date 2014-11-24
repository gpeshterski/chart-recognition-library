package org.crl.utilities;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import org.crl.LibraryLoaderSingleton;
import org.crl.imagedata.Image;

import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRReader {
	/**
	 * Zooms the text image to make it easier to read
	 * */
	public static String recognizeText(Image image) {
		LibraryLoaderSingleton.getInstance();
		Image scaledImage = image.scale(8);
		Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
		instance.setLanguage("eng");
		System.setProperty("jna.encoding", "UTF8");
		instance.setOcrEngineMode(TessAPI.TessOcrEngineMode.OEM_DEFAULT);
		try {
			String result = instance.doOCR(scaledImage.getInnerImage());
			return result;
		} catch (TesseractException e) {
		throw new IllegalStateException(e);
		}
		catch(Exception ex){
			throw new IllegalStateException("An error during text recognition was encountered.");
		}
		
	}
	public static String recognizeXText(Image image) {
		LibraryLoaderSingleton.getInstance();
		Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
		instance.setOcrEngineMode(TessAPI.TessOcrEngineMode.OEM_TESSERACT_ONLY);
		BufferedImage img = getScaledImage(image.getInnerImage(), image.getInnerImage().getWidth()*2, image.getInnerImage().getHeight()*2);  
		img = thresholdImage(img, 165);
		
		try {
			String result = instance.doOCR(img);
			return result;
		} catch (TesseractException e) {
			throw new IllegalStateException(e);
		}
		catch(Exception ex){
			throw new IllegalStateException("An error during text recognition was encountered.");
		}
		
	}
	public static String recognizeYText(Image image) {
		LibraryLoaderSingleton.getInstance();
		Image scaledImage = image.scale(8);
		Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
		instance.setLanguage("eng");
		System.setProperty("jna.encoding", "UTF8");
		instance.setOcrEngineMode(TessAPI.TessOcrEngineMode.OEM_DEFAULT);
		try {
			String result = instance.doOCR(scaledImage.getInnerImage());
			return result;
		} catch (TesseractException e) {
			throw new IllegalStateException(e);
		}
		catch(Exception ex){
			throw new IllegalStateException("An error during text recognition was encountered.");
		}
		
	}

	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) {
	    int imageWidth  = image.getWidth();
	    int imageHeight = image.getHeight();

	    double scaleX = (double)width/imageWidth;
	    double scaleY = (double)height/imageHeight;
	    AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
	    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

	    return bilinearScaleOp.filter(
	        image,
	        new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY));
	}
	
	public static BufferedImage thresholdImage(BufferedImage img,int requiredThresholdValue) {
		int height = img.getHeight();
		int width = img.getWidth();
		BufferedImage finalThresholdImage = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY) ;
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		for (int x = 0; x < width; x++) {
			try {

				for (int y = 0; y < height; y++) {
					int color = img.getRGB(x, y);

					red = ImageOperations.getRed(color);
					green = ImageOperations.getGreen(color);
					blue = ImageOperations.getBlue(color);

					if((red+green+green)/3 < (int) (requiredThresholdValue)) {
							finalThresholdImage.setRGB(x,y,ImageOperations.mixColor(0, 0,0));
						}
						else {
							finalThresholdImage.setRGB(x,y,ImageOperations.mixColor(255, 255,255));
						}
					
				}
			} catch (Exception e) {
				 e.getMessage();
			}
		}
		
		return finalThresholdImage;
	}
}
