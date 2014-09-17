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
public class PlayerView implements IGameRenderer {

	private static final Texture DIGIT_TEXTURE = Texture
			.fromFile("res/digits.png")
			.setFilter(GL11.GL_NEAREST, GL11.GL_NEAREST);
	
	private PlayerModel player;
	private long cashBuffer;
	
	public PlayerView(PlayerModel player) {
		this.player = player;
		cashBuffer = 0;
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
		if (player.getCash() > cashBuffer) {
			if (player.getCash() - cashBuffer > 16) cashBuffer += 16;
			else ++cashBuffer;
		} else if (player.getCash() < cashBuffer) {
			if (player.getCash() - cashBuffer < -16) cashBuffer -= 16;
			else --cashBuffer;
		}
		String cash = cashBuffer+"";
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < cash.length(); ++i) {
			int digit = cash.charAt(i) - '0';
			GL11.glTexCoord2f(3*digit/32f, 6/8f);	GL11.glVertex2f(4*i+0, 0);
			GL11.glTexCoord2f(3*(digit+1)/32f, 6/8f);	GL11.glVertex2f(4*i+3, 0);
			GL11.glTexCoord2f(3*(digit+1)/32f, 0/8f);	GL11.glVertex2f(4*i+3, 6);
			GL11.glTexCoord2f(3*digit/32f, 0/8f);	GL11.glVertex2f(4*i+0, 6);
		}
		GL11.glEnd();
		GL20.glUseProgram(0);
	}
	
	//
	// END
	//

}
