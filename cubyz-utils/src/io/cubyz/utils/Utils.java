package io.cubyz.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import io.cubyz.utils.log.Log;

/**
 * A collection of methods that don't fit anywhere else (yet).
 */

public class Utils {
	public static String idToFile(String ID, String subPath, String ending) {
		String[] parts = ID.split(":");
		if(parts.length != 2) {
			Log.warning(new Exception("Invalid ID \""+ID+"\"! Using default model instead."));
			return "";
		}
		return "assets/"+parts[0]+"/"+subPath+"/"+parts[1]+ending;
	}
	
	public static BufferedImage readImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch(Exception e) {
			Log.severe("Cannot read image in: "+path);
			Log.severe(e);
			return null;
		}
	}
	
	public static BufferedImage writeImage(BufferedImage image, String path) {
		try {
			ImageIO.write(image, "png", new File(path));
			return ImageIO.read(new File(path));
		} catch(Exception e) {
			Log.warning("Cannot write image in: "+path);
			Log.severe(e);
			return null;
		}
	}
}
