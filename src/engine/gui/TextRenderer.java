package engine.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import engine.IGameRenderer;
import engine.graphics.Shader;
import engine.graphics.Texture;

/**
 * @author germangb
 *
 */
public class TextRenderer implements IGameRenderer {
	
	/* graphics */
	private static Texture TEXTURE = Texture.fromFile("fonts/deja.png");
	private static Shader SHADER = Shader.fromFile("shaders/font.vert", "shaders/font.frag");
	
	/* rendered text */
	private String text;
	private int width, height;
	private int rgba;
	
	/**
	 * Default constructor
	 */
	public TextRenderer() {
		this("");
	}
	
	/**
	 * @param text
	 */
	public TextRenderer(String text) {
		setText(text);
		this.rgba = 0xFFFFFFFF;
	}
	
	/**
	 * @param text
	 */
	public void setText (String text) {
		this.text = text;
		String[] apl = text.split("\n");
		this.width = 0;
		this.height = 0;//apl.length * 16;
		for (int i = 0; i < apl.length; ++i)
			this.width = Math.max(width, apl[i].length()*10);
	}
	
	/**
	 * Get the size in pixels
	 * @return
	 */
	public int getDimensionWidth () {
		return width;
	}
	
	/**
	 * Get the size in pixels
	 * @return
	 */
	public int getDimensionHeight () {
		return height;
	}
	
	/**
	 * @param rgba
	 * @return
	 */
	public void setTint (int rgba) {
		this.rgba = rgba;
	}
	
	/**
	 * @return
	 */
	public int getTint () {
		return rgba;
	}
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		TEXTURE.bindTo(0);
		SHADER.bind();
		SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);
		SHADER.uniform1i("texture", 0);
		float r = ((rgba >> 24) & 0xFF) / 255.0f;
		float g = ((rgba >> 16) & 0xFF) / 255.0f;
		float b = ((rgba >> 8) & 0xFF) / 255.0f;
		float a = ((rgba >> 0) & 0xFF) / 255.0f;
		SHADER.uniform4f("tint", r, g, b, a);
		/* inmediate mode... */
		GL11.glBegin(GL11.GL_QUADS);
		float acumX = 0;
		float acumY = 0;
		for (int i = 0; i < text.length(); ++i) {
			int ascii = text.charAt(i);
			int row = ascii / 16;
			int col = ascii % 16;
			if (ascii == '\n') {
				acumX = 0;
				acumY += 16;
			} else {
				/*if (acumX+10 > warp) {
					acumX = 0;
					acumY += 16;
				}*/
				float spac = 8;
				GL11.glTexCoord2f((32*col+1-spac)/512f, (32*row-spac)/512f);	GL11.glVertex2f(acumX+0-spac, acumY+0-spac);
				GL11.glTexCoord2f((32*col+8+1+spac)/512f, (32*row-spac)/512f);	GL11.glVertex2f(acumX+8+spac, acumY+0-spac);
				GL11.glTexCoord2f((32*col+8+1+spac)/512f, (32*row+16+spac)/512f);	GL11.glVertex2f(acumX+8+spac, acumY+16+spac);
				GL11.glTexCoord2f((32*col+1-spac)/512f, (32*row+16+spac)/512f);	GL11.glVertex2f(acumX+0-spac, acumY+16+spac);
				acumX += 10;
			}
		}
		GL11.glEnd();
	}
	
	//
	// END
	//

}
