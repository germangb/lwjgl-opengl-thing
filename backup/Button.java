package game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import resources.Resources;
import engine.IGameRenderer;
import engine.SoundSource;
import engine.SoundSourceObject;
import graphics.Texture;
import gui.Widget;

public class Button extends Widget implements IGameRenderer {

	private static SoundSource CLICK_SOUND = new SoundSource(Resources.get("button-pressed.wav"), true);
	
	private static Texture TEXTURE = Texture
			.fromFile(
					"res/button.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	private SoundSourceObject click;
	
	public Button() {
		dimension().x = 50;
		dimension().y = 16;
		addGameRenderer(this);
		click = new SoundSourceObject("click", CLICK_SOUND, false);
		addChild(click);
	}
	
	//
	// Widget re-implementation
	//
	
	public void action () {
		super.action();
		click.play();
	}

	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int program = Game.SHADER.getProgram();
		GL20.glUseProgram(program);
		int mvpLoc = GL20.glGetUniformLocation(program, "mvp");
		int texLoc = GL20.glGetUniformLocation(program, "texture");
		int transLoc = GL20.glGetUniformLocation(program, "translate");
		FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
		mvp.store(mvpBuffer);
		mvpBuffer.flip();
		GL20.glUniformMatrix4(mvpLoc, false, mvpBuffer);
		GL20.glUniform1i(texLoc, 0);
		GL20.glUniform2f(transLoc, 0, 0);
		GL11.glColor3f(1,1,1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TEXTURE.getId());
		GL11.glBegin(GL11.GL_QUADS);
		int offV = active ? 32 : 0;
		if (!enabled)
			offV = 16;
		GL11.glTexCoord2f(3/128f, (offV+16)/64f);				GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(dimension().x/128f, (offV+16)/64f);	GL11.glVertex2f(dimension().x-3, 0);
		GL11.glTexCoord2f(dimension().x/128f, offV/64f);	GL11.glVertex2f(dimension().x-3, 16);
		GL11.glTexCoord2f(3/128f, offV/64f);				GL11.glVertex2f(0, 16);
		
		GL11.glTexCoord2f(0, (offV+16)/64f);		GL11.glVertex2f(dimension().x-3, 0);
		GL11.glTexCoord2f(3/128f, (offV+16)/64f);	GL11.glVertex2f(dimension().x, 0);
		GL11.glTexCoord2f(3/128f, offV/64f);	GL11.glVertex2f(dimension().x, 16);
		GL11.glTexCoord2f(0, offV/64f);		GL11.glVertex2f(dimension().x-3, 16);
		GL11.glEnd();
		GL20.glUseProgram(0);
	}
	
	//
	// END
	//

}
