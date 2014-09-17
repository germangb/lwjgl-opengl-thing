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
public class InspectorView implements IGameRenderer {

	private static final Texture INSPECTOR_TEXTURE = Texture
			.fromFile("res/inspector.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private static final Texture COLOR_TEXTURE = Texture
			.fromFile("res/color.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);

	
	private InspectorModel inspector;
	
	public InspectorView(InspectorModel inspector) {
		this.inspector = inspector;
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, INSPECTOR_TEXTURE.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);  GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(1, 1);  GL11.glVertex2f(15, 0);
		GL11.glTexCoord2f(1, 0);  GL11.glVertex2f(15, 18);
		GL11.glTexCoord2f(0, 0);  GL11.glVertex2f(0, 18);
		GL11.glEnd();
		
		if (inspector.inspected != null) {
			float red = inspector.inspected.getR() / 255.0f;
			float green = inspector.inspected.getG() / 255.0f;
			float blue = inspector.inspected.getB() / 255.0f;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, COLOR_TEXTURE.getId());
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(1, 0, 0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(3+0, 3+0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(3+1, 3+0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(3+1, 3+12*red);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(3+0, 3+12*red);
			GL11.glColor3f(0, 1, 0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(7+0, 3+0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(7+1, 3+0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(7+1, 3+12*green);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(7+0, 3+12*green);
			GL11.glColor3f(0, 0, 1);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(11+0, 3+0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(11+1, 3+0);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(11+1, 3+12*blue);
			GL11.glTexCoord2f(0.5f, 0.5f);  GL11.glVertex2f(11+0, 3+12*blue);
			GL11.glEnd();
		}
		GL20.glUseProgram(0);
	}
	
	//
	// END
	//

}
