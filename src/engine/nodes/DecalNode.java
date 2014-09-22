package engine.nodes;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;

import engine.Decal;
import engine.GameNode;
import engine.IGameRenderer;
import engine.Scene;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.graphics.PolyModel;
import engine.graphics.Shader;

/**
 * @author germangb
 *
 */
public class DecalNode extends GameNode implements IGameRenderer {

	/* decal shader program */
	private static Shader SHADER = Shader
			.fromFile("shaders/decal.vert", "shaders/decal.frag");
	
	/* avoid calculating the inverse */
	/* this attributes will make sure */
	/* only one inversion per frame is performed */
	private static Matrix4f INV_PROJECTION = new Matrix4f();
	private static int invTick = -1;
	private static PolyModel CUBE = new PolyModel("res/cube.obj");
	
	/* properties */
	private BoxDebugRenderer boxRender;
	private Decal decal;
	private Vector3f size;
	private float opacity;
		
	/**
	 * @param decal
	 */
	public DecalNode(Decal decal) {
		super("decal");
		this.decal = decal;
		this.opacity = 1.0f;
		this.size = new Vector3f(1.0f, 1.0f, 1.0f);
		this.boxRender = new BoxDebugRenderer(1.0f, 1.0f, 1.0f);
		writeGbuffer = false;
		addGameRenderer(this);
		debugRenderer = boxRender;
	}
	
	/**
	 * set size of the decal box
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setSize (float x, float y, float z) {
		size.x = x;
		size.y = y;
		size.z = z;
		this.boxRender.setSize(x, y, z);
	}
	
	/**
	 * set decal opacity
	 * @param opacity new opacity value
	 */
	public void setOpacity (float opacity) {
		this.opacity = opacity;
	}
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		/* buffer that will hold matrix data to push */
		/* to the gpu */
		SHADER.bind();
				
		/* mvp upload */
		SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);

		/* inverse mvp */
		Matrix4f invMvp = new Matrix4f();
		Matrix4f.invert(mvp, invMvp);
		SHADER.uniformMat4("invMvp", false, invMvp);
		
		/* invert projection */
		CameraNode cam = Scene.getInstance().getUsedCamera();
		if (invTick != Framework.getInstance().getTicks()) {
			Matrix4f.invert(cam.getProjectionMatrix(), INV_PROJECTION);
			invTick = Framework.getInstance().getTicks();
		}
		
		//INV_PROJECTION.store(buffer);
		SHADER.uniformMat4("invProj", false, INV_PROJECTION);
		
		/* size upload */
		SHADER.uniform3f("size", size.x, size.y, size.z);
		
		/* opacity upload */
		SHADER.uniform1f("opacity", opacity);
		
		/* depth upload */
		SHADER.uniform1i("depth", 0);
		Scene.getInstance().getDepthTexture().bindTo(0);
		
		/* scene upload */
		SHADER.uniform1i("scene", 1);
		Scene.getInstance().getSceneTexture().bindTo(1);
		
		/* resolution upload */
		//int resLoc = GL20.glGetUniformLocation(program, "resolution");
		float w = Framework.getInstance().getWindowWidth() / Scene.getInstance().getPixelScale();
		float h = Framework.getInstance().getWindowHeight() / Scene.getInstance().getPixelScale();
		SHADER.uniform2f("resolution", w, h);
		
		/* texture */
		SHADER.uniform1i("colorTexture", 2);
		decal.getTexture().bindTo(2);
		SHADER.uniform1i("secondTexture", 3);
		Scene.getInstance().getDataTexture().bindTo(3);

		/* upload light direction */
		Vector3f dir = Scene.getInstance().getShadowCamera().getLook();
		SHADER.uniform3f("dirLight", dir.x, dir.y, dir.z);

		int ambientColor = WorldGlobals.AMBIENT_COLOR;
		int fogColor = WorldGlobals.FOG_COLOR;
		SHADER.uniform1f("fogStart", WorldGlobals.FOG_START);
		SHADER.uniform1f("fogConst", WorldGlobals.FOG_DENSITY);
		SHADER.uniform3f("fogColor", ((fogColor>>16)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);
		SHADER.uniform3f("ambientTint", ((ambientColor>>16)&0xFF)/255.0f, ((ambientColor>>8)&0xFF)/255.0f, ((ambientColor>>0)&0xFF)/255.0f);
		
		/* rasterize box */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, CUBE.vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		
		/* rasterize */
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, CUBE.totalTris*3);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
}
