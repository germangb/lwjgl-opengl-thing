package game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.Camera;
import engine.GameObject;
import engine.IGameRenderer;
import engine.IGameUpdater;
import framework.IResourceLoader;
import framework.Input;
import framework.ResourceManager;
import framework.Scene;
import graphics.Texture;

public class World extends GameObject implements IResourceLoader, IGameRenderer, IGameUpdater {
	
	/* world size */
	private static final int SIZE = 8;
	
	/* Set of isometric tiles */
	private static Texture ISOMETRIC_TILES = Texture
			.fromFile(
					"res/iso.png")
			.setFilter(
					GL11.GL_NEAREST,
					GL11.GL_NEAREST);
	
	public static void unProject (int x, int y, int[] out) {
		/* direct matrix multiplication */
		out[0] = 12*x-12*y - 12 + Game.WIDTH/4;
		out[1] = -6*x-6*y - 6 + Game.HEIGHT/4;
	}
	
	public static void project (int u, int v, int[] out) {
		/* multiply & round up */
		u -= Game.WIDTH/4;
		v -= Game.HEIGHT/4;
		float x = 0.041667f*u-0.083333f*v + 0.5f;
		float y = -0.041667f*u-0.083333f*v + 0.5f;
		if (x < 0.0f) x = -1;
		if (y < 0.0f) y = -1;
		out[0] = (int)x;
		out[1] = (int)y;
	}
	
	/* cursor */
	private int cursorX;
	private int cursorY;
	
	/* OpenGL objects */
	private int vbo;
	private int uvbo;
	
	/**
	 * Instantiate a world
	 * Default constructor
	 */
	public World() {
		super("isometric_world");
		ResourceManager.addResources(this);
		addGameRenderer(this);
		addGameUpdater(this);
		cursorX = 0;
		cursorY = 0;
	}
	
	/**
	 * set cursor position
	 * @param x new X
	 * @param y new Y
	 */
	public void setCursor (int x, int y) {
		this.cursorX = x;
		this.cursorY = y;
		if (cursorX < 0 || cursorX >= SIZE) cursorX = -1;
		if (cursorY < 0 || cursorY >= SIZE) cursorY = -1;
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ISOMETRIC_TILES.getId());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvbo);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 6, 6*SIZE*SIZE);
		
		if (cursorX != -1 && cursorY != -1) {
			int[] cursorPos = new int[2];
			unProject(cursorX, cursorY, cursorPos);
			GL20.glUniform2f(transLoc, cursorPos[0], cursorPos[1]+1);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		}
		
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL20.glUseProgram(0);
	}

	//
	// IResourceLoaderinterface implementation
	//
	
	@Override
	public boolean loadResources() {
		vbo = GL15.glGenBuffers();
		uvbo = GL15.glGenBuffers();
		FloatBuffer position = BufferUtils.createFloatBuffer(2*3*2 * (SIZE*SIZE+1));
		FloatBuffer uv = BufferUtils.createFloatBuffer(2*3*2 * (SIZE*SIZE+1));
		
		position.put(new float[]{
			 0,  0,
			26,  0,
			26, 13,
			
			 0,  0,
			26, 13,
			 0, 13
		});
		uv.put(new float[]{
			0,      26/32f,
			26/32f, 26/32f,
			26/32f, 13/32f,
				
			0,      26/32f,
			26/32f, 13/32f,
			0,      13/32f
		});
		for (int i = 0; i < SIZE; ++i)
			for (int x = 0; x < SIZE; ++x) {
				int[] proj = new int[2];
				unProject(i, x, proj);
				position.put(new float[]{
					proj[0]+ 0, proj[1]+ 0,
					proj[0]+26, proj[1]+ 0,
					proj[0]+26, proj[1]+13,
					
					proj[0]+ 0, proj[1]+ 0,
					proj[0]+26, proj[1]+13,
					proj[0]+ 0, proj[1]+13
				});
				uv.put(new float[]{
					0,      13/32f,
					26/32f, 13/32f,
					26/32f, 0,
						
					0,      13/32f,
					26/32f, 0,
					0,      0
				});
			}
		position.flip();
		uv.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, position, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uv, GL15.GL_STATIC_DRAW);
		position.clear();
		uv.clear();
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(uvbo);
	}
	
	//
	//
	//

	@Override
	public void update(GameObject object) {
		Camera camera = Scene.getInstance().getUsedCamera();
		if (GameController.getInstance().getState() == GameState.WORLD) {
			int mx = (int)(Input.getMouseX()/2 + camera.position().x);
			int my = (int)(Input.getMouseY()/2 + camera.position().y);
			int[] c = new int[2];
			project(mx, my, c);
			setCursor(c[0], c[1]);
		} else setCursor(-1, -1);
	}
	
	//
	// END
	//

}
