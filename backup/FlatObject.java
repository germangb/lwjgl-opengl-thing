package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import framework.Framework;
import framework.IResourceLoader;
import framework.LinearUtils;
import framework.ResourceManager;
import graphics.Shader;

/**
 * @author germangb
 *
 */
public abstract class FlatObject extends GameObject implements IGameRenderer, IResourceLoader {
	
	private static final Shader SHADER =
			Shader.fromFile("shaders/flat.vert", "shaders/flat.frag");
	private static Matrix4f PROJECTION;
	
	/**
	 * Create a game object
	 * @param key game object key
	 */
	public FlatObject(String key) {
		super(key);
		ResourceManager.addResources(this);
		/* add renderer */
		addGameRenderer(this);
	}

	/**
	 * Called to render the geometry
	 */
	public abstract void renderFlat ();
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public boolean loadResources() {
		/* create projection matrix */
		Framework frame = Framework.getInstance();
		PROJECTION = LinearUtils.ortho(0, frame.getWindowWidth(), 0, frame.getWindowHeight(), -1, 1);
		return true;
	}

	@Override
	public void cleanResources() {
		PROJECTION = null;
	}
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		/* shader program */
		int program = SHADER.getProgram();
		
		/* bind program */
		GL20.glUseProgram(program);
				
		/* create a new mvp and upload */
		Matrix4f modelViewProjection;
		modelViewProjection = LinearUtils.getModelViewProjection(PROJECTION, new Matrix4f(), getModelTransformation());
		int mvpLoc = GL20.glGetUniformLocation(program, "modelViewProjectionMatrix");
		FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
		modelViewProjection.store(mvpBuffer);
		mvpBuffer.flip();
		GL20.glUniformMatrix4(mvpLoc, false, mvpBuffer);
		mvpBuffer.clear();
		
		/* texture uniform */
		int textureLoc = GL20.glGetUniformLocation(program, "texture");
		GL20.glUniform1i(textureLoc, 0);
		
		/* set up gl */
		GL11.glDisable(GL11.GL_CULL_FACE);
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		/* render plat stuff */
		renderFlat();
		
		/* unbind program */
		GL20.glUseProgram(0);
	}
	
	//
	// END
	//

}
