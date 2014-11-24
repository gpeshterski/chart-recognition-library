package org.crl.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageUtils {

	private static final Log logger = LogFactory.getLog(ImageUtils.class); 
	public static void saveImage(BufferedImage image, String file){
		try {
		    // retrieve image
		    File outputfile = new File(file+".png");
		    if(outputfile.exists()){
		    	outputfile=new File(file+new Date().getTime()+".png");
		    }
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			logger.info("Unable to save image to: "+file);
		}
	}
}
