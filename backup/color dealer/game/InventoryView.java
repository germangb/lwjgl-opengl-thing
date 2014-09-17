package game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.IGameRenderer;
import graphics.Texture;

/**
 * @author germangb
 *
 */
public class InventoryView implements IGameRenderer {

	private static final Texture TEXTURE = Texture
			.fromFile("res/inventory.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private static final Texture POINTER_TEXTURE = Texture
			.fromFile("res/pointer.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private static final Texture COLOR_TEXTURE = Texture
			.fromFile("res/color.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private InventoryModel invent;
	
	/**
	 * @param inventory
	 */
	public InventoryView(InventoryModel inventory) {
		this.invent = inventory;
	}

	//
	// IGameRenderer interface implementation
	//
	
	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		int program = Game.SHADER.getProgram();
		GL20.glUseProgram(program);
		int textureLocation = GL20.glGetUniformLocation(program, "texture");
		int mvpLocation = GL20.glGetUniformLocation(program, "mvp");
		FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
		mvp.store(mvpBuffer);
		mvpBuffer.flip();
		GL20.glUniform1i(textureLocation, 0);
		GL20.glUniformMatrix4(mvpLocation, false, mvpBuffer);
		mvpBuffer.clear();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		/* render slots */
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TEXTURE.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(58, 0);
		GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(58, 79);
		GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0, 79);
		GL11.glEnd();
				
		/* render pointer */
		if (invent.col != -1 || invent.row != -1) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, POINTER_TEXTURE.getId());
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0 + 10 * invent.col, Game.HEIGHT/4 - 11 + 0 - 10 * invent.row);
			GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(8 + 10 * invent.col, Game.HEIGHT/4 - 11 + 0 - 10 * invent.row);
			GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(8 + 10 * invent.col, Game.HEIGHT/4 - 11 + 8 - 10 * invent.row);
			GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0 + 10 * invent.col, Game.HEIGHT/4 - 11 + 8 - 10 * invent.row);
			GL11.glEnd();
		}
		
		/* render colors */
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, COLOR_TEXTURE.getId());
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < invent.rows(); ++i) {
			for (int x = 0; x < invent.cols(); ++x) {
				Item it = invent.itemAt(i, x);
				if (it != null) {
					float red = it.getR() / 255.0f;
					float green = it.getG() / 255.0f;
					float blue = it.getB() / 255.0f;
					GL11.glColor3f(red, green, blue);
					GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0 + 10 * x, Game.HEIGHT/4 - 11 + 0 - 10 * i);
					GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(8 + 10 * x, Game.HEIGHT/4 - 11 + 0 - 10 * i);
					GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(8 + 10 * x, Game.HEIGHT/4 - 11 + 8 - 10 * i);
					GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0 + 10 * x, Game.HEIGHT/4 - 11 + 8 - 10 * i);
				}
			}
		}
		GL11.glEnd();
		
		GL20.glUseProgram(0);
	}
	
	//
	// END
	//

}
