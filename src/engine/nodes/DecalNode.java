package engine.nodes;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.Decal;
import engine.GameNode;
import engine.IGameRenderer;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.framework.Scene;
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
		setDebugRenderer(boxRender);
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
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		int program = SHADER.getProgram();
		SHADER.bind();
				
		/* mvp upload */
		int mvpLoc = GL20.glGetUniformLocation(program, "modelViewProjectionMatrix");
		mvp.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(mvpLoc, false, buffer);

		/* inverse mvp */
		Matrix4f invMvp = new Matrix4f();
		Matrix4f.invert(mvp, invMvp);
		invMvp.store(buffer);
		buffer.flip();
		int imvpLocation = GL20.glGetUniformLocation(program, "invMvp");
		GL20.glUniformMatrix4(imvpLocation, false, buffer);
		
		/* invert projection */
		CameraNode cam = Scene.getInstance().getUsedCamera();
		if (invTick != Framework.getInstance().getTicks()) {
			Matrix4f.invert(cam.getProjectionMatrix(), INV_PROJECTION);
			invTick = Framework.getInstance().getTicks();
		}
		INV_PROJECTION.store(buffer);
		buffer.flip();
		int invProjLocation = GL20.glGetUniformLocation(program, "invProj");
		GL20.glUniformMatrix4(invProjLocation, false, buffer);
		
		/* size upload */
		int sizeLocation = GL20.glGetUniformLocation(program, "size");
		GL20.glUniform3f(sizeLocation, size.x, size.y, size.z);
		
		/* opacity upload */
		int opaLocation = GL20.glGetUniformLocation(program, "opacity");
		GL20.glUniform1f(opaLocation, opacity);
		
		/* depth upload */
		int depthLoc = GL20.glGetUniformLocation(program, "depth");
		GL20.glUniform1i(depthLoc, 0);
		Scene.getInstance().getDepthTexture().bindTo(0);
		//GL13.glActiveTexture(GL13.GL_TEXTURE0);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, Scene.getInstance().getDepthTexture().getId());
		
		/* scene upload */
		int sceneLoc = GL20.glGetUniformLocation(program, "scene");
		GL20.glUniform1i(sceneLoc, 1);
		Scene.getInstance().getSceneTexture().bindTo(1);
		//GL13.glActiveTexture(GL13.GL_TEXTURE1);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, Scene.getInstance().getSceneTexture().getId());
		
		/* resolution upload */
		int resLoc = GL20.glGetUniformLocation(program, "resolution");
		float w = Framework.getInstance().getWindowWidth() / Scene.getInstance().getPixelScale();
		float h = Framework.getInstance().getWindowHeight() / Scene.getInstance().getPixelScale();
		GL20.glUniform2f(resLoc, w, h);
		
		/* texture */
		int decalTextureLoc = GL20.glGetUniformLocation(program, "colorTexture");
		GL20.glUniform1i(decalTextureLoc, 2);
		decal.getTexture().bindTo(2);
		//GL13.glActiveTexture(GL13.GL_TEXTURE2);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, decal.getTexture().getId());

		/* upload light direction */
		int lightLocation = GL20.glGetUniformLocation(program, "dirLight");
		Vector3f dir = Scene.getInstance().getShadowCamera().getLook();
		GL20.glUniform3f(lightLocation, dir.x, dir.y, dir.z);

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
		
		/* rasterize box */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, CUBE.vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		
		/* rasterize */
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, CUBE.totalTris*3);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		buffer.clear();
	}
}
