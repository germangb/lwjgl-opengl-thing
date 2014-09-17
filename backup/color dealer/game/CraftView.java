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
public class CraftView implements IGameRenderer {

	private static final Texture DIGIT_TEXTURE = Texture
			.fromFile("res/craft.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private static final Texture POINTER_TEXTURE = Texture
			.fromFile("res/pointer.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private static final Texture COLOR_TEXTURE = Texture
			.fromFile("res/color.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private CraftModel craft;
	
	public CraftView(CraftModel craft) {
		this.craft = craft;
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, DIGIT_TEXTURE.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(50, 0);
		GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(50, 18);
		GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0, 18);
		GL11.glEnd();
		
		if (craft.pointer == 0 || craft.pointer == 1 || craft.pointer == 2) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, POINTER_TEXTURE.getId());
			if (craft.pointer == 2) {
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0 + 7 + 10*3, 0 + 5);
				GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(8 + 7 + 10*3, 0 + 5);
				GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(8 + 7 + 10*3, 8 + 5);
				GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0 + 7 + 10*3, 8 + 5);
				GL11.glEnd();
			} else {
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0 + 5 + 10*craft.pointer, 0 + 5);
				GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(8 + 5 + 10*craft.pointer, 0 + 5);
				GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(8 + 5 + 10*craft.pointer, 8 + 5);
				GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0 + 5 + 10*craft.pointer, 8 + 5);
				GL11.glEnd();
			}
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, COLOR_TEXTURE.getId());
		if (craft.getU() != null) {
			float red = craft.getU().getR() / 255.0f;
			float green = craft.getU().getG() / 255.0f;
			float blue = craft.getU().getB() / 255.0f;
			GL11.glColor3f(red, green, blue);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(red, green, blue);
			GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0 + 5, 0 + 5);
			GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(8 + 5, 0 + 5);
			GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(8 + 5, 8 + 5);
			GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0 + 5, 8 + 5);
			GL11.glEnd();
		}
		
		if (craft.getV() != null) {
			float red = craft.getV().getR() / 255.0f;
			float green = craft.getV().getG() / 255.0f;
			float blue = craft.getV().getB() / 255.0f;
			GL11.glColor3f(red, green, blue);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(red, green, blue);
			GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0 + 5 + 10, 0 + 5);
			GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(8 + 5 + 10, 0 + 5);
			GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(8 + 5 + 10, 8 + 5);
			GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0 + 5 + 10, 8 + 5);
			GL11.glEnd();
		}
		
		if (craft.crafted() != null) {
			float red = craft.crafted().getR() / 255.0f;
			float green = craft.crafted().getG() / 255.0f;
			float blue = craft.crafted().getB() / 255.0f;
			GL11.glColor3f(red, green, blue);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(red, green, blue);
			GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0 + 5 + 32, 0 + 5);
			GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(8 + 5 + 32, 0 + 5);
			GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(8 + 5 + 32, 8 + 5);
			GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0 + 5 + 32, 8 + 5);
			GL11.glEnd();
		}
		
		GL20.glUseProgram(0);
	}
	
	//
	// END
	//

}
