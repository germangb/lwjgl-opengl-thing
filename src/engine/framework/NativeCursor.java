package engine.framework;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;

/**
 * @author germangb
 *
 * LWJGL's Cursor class wrapper
 */
public class NativeCursor {

	/**
	 * LWJGL native cursor
	 */
	private Cursor cursor;
	
	/**
	 * @param image
	 * @param hotX
	 * @param hotY
	 */
	public NativeCursor(BufferedImage image, int hotX, int hotY) {
		try {
			int w = image.getWidth();
			int h = image.getHeight();
			int[] pixx = image.getRGB(0, 0, w, h, (int[])null, 0, w);
			IntBuffer bt = BufferUtils.createIntBuffer(w*h);
			bt.put(pixx);
			bt.flip();
			cursor = new Cursor(w, h, hotX, hotY, 1, bt, null);
			bt.clear();
		} catch (LWJGLException e) {
			/* output to the error stream */
			e.printStackTrace(Framework.getInstance().getErrStream());
		}
	}
	
	/**
	 * get cursor reference
	 * @return LWJGL native cursor reference
	 */
	public Cursor getCursor () {
		return cursor;
	}

}
