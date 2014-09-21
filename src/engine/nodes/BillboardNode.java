package engine.nodes;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.GameNode;
import engine.IGameRenderer;
import engine.Scene;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.framework.LinearUtils;
import engine.graphics.Shader;

/**
 * @author germangb
 *
 */
public abstract class BillboardNode extends GameNode implements IGameRenderer {

	/**
	 * billboard shader instance
	 */
	private static final Shader BILLBOARD_SHADER =
			Shader.fromFile("shaders/billboard.vert", "shaders/billboard.frag");
	
	/**
	 * Create a game object
	 * @param key game object key
	 */
	public BillboardNode(String key) {
		super(key);
		/* add renderer */
		addGameRenderer(this);
	}
	
	/**
	 * Render the billboard
	 */
	public abstract void renderBillboard ();
	
	//
	// IGameRenderer interface implementation
	//
	
	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		/* get camera */
		Scene scene = Scene.getInstance();
		CameraNode camera = scene.getUsedCamera();
		
		/* shader program */
		int program = BILLBOARD_SHADER.getProgram();
		
		/* bind program */
		BILLBOARD_SHADER.bind();
		
		/* modified model matrix */
		Matrix4f modelMatrix = new Matrix4f();
		Vector3f pos = getWorldPosition();
		modelMatrix.translate(new org.lwjgl.util.vector.Vector3f(pos.x, pos.y, pos.z));
		Vector3f rotation = camera.getRotation();
		modelMatrix.rotate(-rotation.y, new org.lwjgl.util.vector.Vector3f(0, 1, 0));
		modelMatrix.rotate(-rotation.x, new org.lwjgl.util.vector.Vector3f(1, 0, 0));
		
		/* create a new mvp and upload */
		Matrix4f modelViewProjection;
		modelViewProjection = LinearUtils.getModelViewProjection(camera.getProjectionMatrix(), v, modelMatrix);
		int mvpLoc = GL20.glGetUniformLocation(program, "modelViewProjectionMatrix");
		FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
		modelViewProjection.store(mvpBuffer);
		mvpBuffer.flip();
		GL20.glUniformMatrix4(mvpLoc, false, mvpBuffer);
		mvpBuffer.clear();
		
		/* mv upload */
		int mvLoc = GL20.glGetUniformLocation(program, "modelViewMatrix");
		FloatBuffer mvBuffer = BufferUtils.createFloatBuffer(16);
		mv.store(mvBuffer);
		mvBuffer.flip();
		GL20.glUniformMatrix4(mvLoc, false, mvBuffer);
		mvBuffer.clear();
		
		/* texture uniform */
		int textureLoc = GL20.glGetUniformLocation(program, "texture");
		GL20.glUniform1i(textureLoc, 0);
		
		
		/* upload time */
		int timeLoc = GL20.glGetUniformLocation(program, "time");
		GL20.glUniform1f(timeLoc, Framework.getInstance().getLocalTime()*0.001f);
		
		/* upload fog start */
		int fogStartLocation = GL20.glGetUniformLocation(program, "fogStart");
		GL20.glUniform1f(fogStartLocation, WorldGlobals.FOG_START);
		
		/* upload fog constant */
		int fogConstLocation = GL20.glGetUniformLocation(program, "fogConst");
		GL20.glUniform1f(fogConstLocation, WorldGlobals.FOG_DENSITY);
		
		/* upload fog color */
		int fogColorLocation = GL20.glGetUniformLocation(program, "fogColor");
		int fogColor = WorldGlobals.FOG_COLOR;
		GL20.glUniform3f(fogColorLocation, ((fogColor>>16)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);

		/* upload tint color */
		int tintColorLocation = GL20.glGetUniformLocation(program, "ambientTint");
		int ambientColor = WorldGlobals.AMBIENT_COLOR;
		GL20.glUniform3f(tintColorLocation, ((ambientColor>>16)&0xFF)/255.0f, ((ambientColor>>8)&0xFF)/255.0f, ((ambientColor>>0)&0xFF)/255.0f);
		
		renderBillboard();
	}

	//
	// END
	//
	
}
