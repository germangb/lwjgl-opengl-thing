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
		this.text = text;
	}
	
	/**
	 * @param text
	 */
	public void setText (String text) {
		this.text = text;
	}
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		TEXTURE.bindTo(0);
		SHADER.bind();
		SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);
		SHADER.uniform1i("testure", 0);
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
				float spac = 16;
				GL11.glTexCoord2f((32*col+1-spac)/512f, 32*row/512f);	GL11.glVertex2f(acumX+0-spac, acumY+0);
				GL11.glTexCoord2f((32*col+8+1+spac)/512f, 32*row/512f);	GL11.glVertex2f(acumX+8+spac, acumY+0);
				GL11.glTexCoord2f((32*col+8+1+spac)/512f, (32*row+16)/512f);	GL11.glVertex2f(acumX+8+spac, acumY+16);
				GL11.glTexCoord2f((32*col+1-spac)/512f, (32*row+16)/512f);	GL11.glVertex2f(acumX+0-spac, acumY+16);
				acumX += 10;
			}
		}
		GL11.glEnd();
	}
	
	//
	// END
	//

}
