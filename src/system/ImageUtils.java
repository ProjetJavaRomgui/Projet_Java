package system;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A utility class for working with external images (placed as inner class so
 * less overwhelming)
 */
public class ImageUtils {

	/**
	 * Loads an image object with the given filename.
	 *
	 * @param filename
	 *            The path and filename of the image to load
	 * @return An Image object representing that image.
	 */
	public static Image loadImage(String filename) {
		Image img = null;

		try {
			img = ImageIO.read(new File(filename)); // read the image from a
													// file
		} catch (IOException e) {
			System.err.println("Error loading \'" + filename + "\': " + e.getMessage());
		}
		return img; // return the image
	}
}