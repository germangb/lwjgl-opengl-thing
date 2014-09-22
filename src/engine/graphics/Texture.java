package engine.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import engine.IResourceLoader;
import engine.ResourceManager;

/**
 * @author germangb
 * 
 */
public class Texture implements IResourceLoader {
	
	/**
	 * Map that holds all textures requested
	 * by the framework.
	 */
	private static Map<String, Texture> files = new HashMap<String, Texture>();

	/**
	 * binded texture units
	 */
	private static int[] binded = new int[8];
	
	/**
	 * @param file
	 * @return texture instance
	 */
	public static Texture fromFile (String path) {
		Texture tex = files.get(path);
		if (tex == null) {
			tex = new Texture();
			tex.source = path;
			ResourceManager.addResources(tex);
			files.put(path, tex);
		}
		return tex;
	}
	
	/* texture params */
	private int id;
	private int magFilter;
	private int minFilter;
	private int warpS, warpT;
	private String source;

	/**
	 * Default constructor
	 */
	public Texture() {
		magFilter = GL11.GL_NEAREST;
		minFilter = GL11.GL_LINEAR_MIPMAP_LINEAR;
		warpS = GL11.GL_REPEAT;
		warpT = GL11.GL_REPEAT;
	}

	/**
	 * @param context
	 * @param id
	 */
	public Texture(int id) {
		this.id = id;
	}
	
	/**
	 * Set texture filtering
	 * @param min
	 * @param mag
	 * @return
	 */
	public Texture setFilter (int min, int mag) {
		this.magFilter = mag;
		this.minFilter = min;
		return this;
	}
	
	/**
	 * @param s
	 * @param t
	 * @return
	 */
	public Texture setWarp (int s, int t) {
		this.warpS = s;
		this.warpT = t;
		return this;
	}
	
	/**
	 * bind texture to a specific
	 * texture unit
	 * 
	 * @param unit texture unit
	 */
	public void bindTo (int unit) {
		if (binded[unit] != id) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+unit);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			binded[unit] = id;
		}
	}
	
	/**
	 * @return texture id
	 */
	/*public int getId () {
		return id;
	}*/
	
	/*public String getSource () {
		return source;
	}*/

	//
	// IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		/* reset error */
		GL11.glGetError();
		
		try {
			BufferedImage bimage = ImageIO.read(new File(source));
			int w = bimage.getWidth();
			int h = bimage.getHeight();
			int[] pixel = bimage.getRGB(0, 0, w, h, null, 0, w);
			ByteBuffer buff = BufferUtils.createByteBuffer(w * h * 4);
			for (int i = 0; i < w * h; ++i) {
				byte r = (byte) (pixel[i] >> 16);
				byte g = (byte) (pixel[i] >> 8);
				byte b = (byte) (pixel[i] >> 0);
				byte a = (byte) (pixel[i] >> 24);
				buff.put(r).put(g).put(b).put(a);
			}
			buff.flip();
			id = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, buff);
			buff.clear();
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, warpS);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, warpT);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		} catch (IOException e) {
			//PrintStream err = Framework.getInstance().getErrStream();
			//e.printStackTrace(err);
			return false;
		}
		
		/* final error check */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL11.glDeleteTextures(id);
	}
	
	//
	// END
	//

}
