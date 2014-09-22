package engine.nodes;

import javax.vecmath.Vector3f;
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
	private static final Shader SHADER =
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
		
		/* bind program */
		SHADER.bind();
		
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
		SHADER.uniformMat4("modelViewProjectionMatrix", false, modelViewProjection);
		
		/* mv upload */
		SHADER.uniformMat4("modelViewMatrix", false, mv);
		
		/* texture uniform */
		SHADER.uniform1i("texture", 0);
		
		int ambientColor = WorldGlobals.AMBIENT_COLOR;
		int fogColor = WorldGlobals.FOG_COLOR;
		SHADER.uniform1f("time", Framework.getInstance().getLocalTime()*0.001f);
		SHADER.uniform1f("fogStart", WorldGlobals.FOG_START);
		SHADER.uniform1f("fogConst", WorldGlobals.FOG_DENSITY);
		SHADER.uniform3f("fogColor", ((fogColor>>16)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);
		SHADER.uniform3f("ambientTint", ((ambientColor>>16)&0xFF)/255.0f, ((ambientColor>>8)&0xFF)/255.0f, ((ambientColor>>0)&0xFF)/255.0f);
		
		renderBillboard();
	}

	//
	// END
	//
	
}
