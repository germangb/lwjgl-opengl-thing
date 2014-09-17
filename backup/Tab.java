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

public class Tab extends Widget implements IGameRenderer {

	private static SoundSource CLICK_SOUND = new SoundSource(Resources.get("bup.wav"), true);
	
	private static Texture TEXTURE = Texture
			.fromFile(
					"res/tab.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	public boolean selected;
	private SoundSourceObject click;
	
	public Tab() {
		dimension().x = 32;
		dimension().y = 14;
		addGameRenderer(this);
		this.selected = false;
		click = new SoundSourceObject("click", CLICK_SOUND, false);
		addChild(click);
	}

	//
	// Widget re-implementation
	//
	
	public void action () {
		if (!selected)
			click.play();
		super.action();
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
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TEXTURE.getId());
		GL11.glBegin(GL11.GL_QUADS);
		int offV = selected ? 14 : 0;
		GL11.glTexCoord2f(3/128f, (offV+14)/32f);				GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(dimension().x/128f, (offV+14)/32f);	GL11.glVertex2f(dimension().x-3, 0);
		GL11.glTexCoord2f(dimension().x/128f, offV/32f);	GL11.glVertex2f(dimension().x-3, 14);
		GL11.glTexCoord2f(3/128f, offV/32f);				GL11.glVertex2f(0, 14);
		
		GL11.glTexCoord2f(0, (offV+14)/32f);		GL11.glVertex2f(dimension().x-3, 0);
		GL11.glTexCoord2f(3/128f, (offV+14)/32f);	GL11.glVertex2f(dimension().x, 0);
		GL11.glTexCoord2f(3/128f, offV/32f);	GL11.glVertex2f(dimension().x, 14);
		GL11.glTexCoord2f(0, offV/32f);		GL11.glVertex2f(dimension().x-3, 14);
		GL11.glEnd();
		GL20.glUseProgram(0);
	}
	
	//
	// END
	//

}
