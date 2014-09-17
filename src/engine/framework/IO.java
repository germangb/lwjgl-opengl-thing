package engine.framework;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;

/**
 * @author germangb
 *
 */
public class IO {

	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static String readTextFile (String file) throws FileNotFoundException {
		StringBuilder builder = new StringBuilder();
		Scanner scan = new Scanner(new FileReader(file));
		while (scan.hasNextLine())
			builder.append(scan.nextLine()+'\n');
		scan.close();
		
		return builder.toString();
	}
	
	/**
	 * Convert pixel data to RGBA
	 * each RGBA is stores in an integer
	 * @param img
	 * @return int buffer RGBA format
	 */
	public static IntBuffer imageToIntBuffer (BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		IntBuffer buff = BufferUtils.createIntBuffer(w*h);
		for (int i = 0; i < 16*16; ++i) {
			int pixel = img.getRGB(i%w, i/w);	// ARGB
			int rgba = ((pixel >> 16)&0xFF) |
					   ((pixel >>  8)&0xFF) |
					   ((pixel >>  0)&0xFF) |
					   ((pixel>>24)&0xFF);
			buff.put(rgba);
		}
		buff.flip();
		return buff;
	}
	
	/**
	 * Convert pixel data to RGBA
	 * each RGBA is stores in 4 separated
	 * bytes
	 * @param img
	 * @return byte buffer RGBA buffer
	 */
	public static ByteBuffer imageToByteBuffer (BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		ByteBuffer buff = BufferUtils.createByteBuffer(w*h*4);
		for (int i = 0; i < 16*16; ++i) {
			int pixel = img.getRGB(i%16, i/16);	// ARGB
			buff.put((byte)((pixel>>16)&0xFF))
				  .put((byte)((pixel>>8)&0xFF))
				  .put((byte)((pixel>>0)&0xFF))
				  .put((byte)((pixel>>24)&0xFF));
		}
		buff.flip();
		return buff;
	}

}
