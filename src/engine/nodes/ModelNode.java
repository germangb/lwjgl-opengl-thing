package engine.nodes;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.GameNode;
import engine.IGameRenderer;
import engine.Scene;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.framework.LinearUtils;
import engine.graphics.PolyModel;
import engine.graphics.Shader;
import engine.graphics.Texture;

/**
 * @author germangb
 *
 */
public class ModelNode extends GameNode implements IGameRenderer {

	/**
	 * OpenGL shader
	 */
	private final static Shader SHADER =
			Shader.fromFile("shaders/model.vert", "shaders/model.frag");
	
	private Texture texture;
	private PolyModel model;
	private PolyModel smooth;
	private boolean highlight;
	
	/**
	 * Creates the game object
	 * @param key
	 */
	public ModelNode(String key, PolyModel model, PolyModel smooth, Texture texture) {
		super(key);
		this.model = model;
		this.smooth = smooth;
		/* add renderer */
		addGameRenderer(this);
		this.texture = texture;
		if (texture != null)
			texture.setFilter(GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
	}
	
	/**
	 * @param key
	 * @param model
	 * @param smooth
	 * @param texture
	 */
	public ModelNode(String key, PolyModel model, Texture texture) {
		this(key, model, null, texture);
		this.model = model;
		/* add renderer */
		addGameRenderer(this);
		this.texture = texture;
		if (texture != null)
			texture.setFilter(GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
	}
	
	/**
	 * tell the shader to highlight this
	 * 3d model
	 * @param b new state
	 */
	public void setHighlighted (boolean b) {
		this.highlight = b;
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

		/* bind shader */
		SHADER.bind();
		
		/* mvp upload */
		int mvpLoc = GL20.glGetUniformLocation(program, "modelViewProjectionMatrix");
		mvp.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(mvpLoc, false, buffer);
		
		/* texture binds */
		int refStrLoc = GL20.glGetUniformLocation(program, "colorTexture");
		GL20.glUniform1i(refStrLoc, 0);
		
		/* mv upload */
		int mvLoc = GL20.glGetUniformLocation(program, "modelViewMatrix");
		mv.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(mvLoc, false, buffer);
		
		/* view upload */
		int vLoc = GL20.glGetUniformLocation(program, "viewMatrix");
		v.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(vLoc, false, buffer);
		
		/* upload time */
		int timeLoc = GL20.glGetUniformLocation(program, "time");
		GL20.glUniform1f(timeLoc, Framework.getInstance().getLocalTime()*0.001f);
		
		/* upload fog start */
		int fogStartLocation = GL20.glGetUniformLocation(program, "fogStart");
		GL20.glUniform1f(fogStartLocation, WorldGlobals.FOG_START);
		
		/* upload fog constant */
		int fogConstLocation = GL20.glGetUniformLocation(program, "fogConst");
		GL20.glUniform1f(fogConstLocation, WorldGlobals.FOG_DENSITY);
		
		/* upload light direction */
		int lightLocation = GL20.glGetUniformLocation(program, "dirLight");
		Vector3f dir = Scene.getInstance().getShadowCamera().getLook();
		GL20.glUniform3f(lightLocation, dir.x, dir.y, dir.z);
		
		/* upload fog color */
		int fogColorLocation = GL20.glGetUniformLocation(program, "fogColor");
		int fogColor = WorldGlobals.FOG_COLOR;
		GL20.glUniform3f(fogColorLocation, ((fogColor>>16)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);
		
		/* upload tint color */
		int tintColorLocation = GL20.glGetUniformLocation(program, "ambientTint");
		int ambientColor = WorldGlobals.AMBIENT_COLOR;
		GL20.glUniform3f(tintColorLocation, ((ambientColor>>16)&0xFF)/255.0f, ((ambientColor>>8)&0xFF)/255.0f, ((ambientColor>>0)&0xFF)/255.0f);
		
		/* upload highlight */
		int highlocation = GL20.glGetUniformLocation(program, "highlight");
		GL20.glUniform1i(highlocation, 0);
		
		/* upload shadow */
		int shadowsLocation = GL20.glGetUniformLocation(program, "renderShadows");
		GL20.glUniform1i(shadowsLocation, Scene.getInstance().renderShadows() ? 1 : 0);
	
		/* shadow ViewProjection upload */
		Matrix4f mfix = new Matrix4f();
		mfix.m00 = 0.5f;	mfix.m10 = 0.0f;	mfix.m20 = 0.0f;	mfix.m30 = 0.5f;
		mfix.m01 = 0.0f;	mfix.m11 = 0.5f;	mfix.m21 = 0.0f;	mfix.m31 = 0.5f;
		mfix.m02 = 0.0f;	mfix.m12 = 0.0f;	mfix.m22 = 0.5f;	mfix.m32 = 0.5f;
		mfix.m03 = 0.0f;	mfix.m13 = 0.0f;	mfix.m23 = 0.0f;	mfix.m33 = 1.0f;
		int smvLoc = GL20.glGetUniformLocation(program, "shadowMatrix");
		CameraNode shadowCamera = Scene.getInstance().getShadowCamera();
		Matrix4f shadowViewProj = LinearUtils.getModelViewProjection(shadowCamera, this);
		Matrix4f shadowMatrix = new Matrix4f();
		Matrix4f.mul(mfix, shadowViewProj, shadowMatrix);
		shadowMatrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(smvLoc, false, buffer);
		
		/* shadow map upload */
		int shadowMapLoc = GL20.glGetUniformLocation(program, "shadowMap");
		GL20.glUniform1i(shadowMapLoc, 1);
		int shadowMapTexture = Scene.getInstance().getShadowMap().getId();
		
		/* bind texture */
		if (texture != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
		}
			
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapTexture);
		
		/* draw model */
		int vbo = model.vbo;
		int nbo = model.nbo;
		int uvbo = model.uvbo;
		int totalTris = model.totalTris;
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL20.glEnableVertexAttribArray(Shader.POSITION_ATTRIB);
		GL20.glVertexAttribPointer(Shader.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);
		GL20.glVertexAttribPointer(Shader.NORMAL_ATTRIB, 3, GL11.GL_FLOAT, true, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvbo);
		GL20.glEnableVertexAttribArray(Shader.UV0_ATTRIB);
		GL20.glVertexAttribPointer(Shader.UV0_ATTRIB, 2, GL11.GL_FLOAT, false, 0, 0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3*totalTris);
		if (highlight) {
			if (smooth != null) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, smooth.nbo);
				GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);
				GL20.glVertexAttribPointer(Shader.NORMAL_ATTRIB, 3, GL11.GL_FLOAT, true, 0, 0);
			}
			//GL11.glDepthMask(false);
			GL20.glUniform1i(highlocation, 1);
			GL11.glCullFace(GL11.GL_FRONT);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3*totalTris);
			GL11.glDepthMask(true);
		}
		GL20.glDisableVertexAttribArray(Shader.UV0_ATTRIB);
		GL20.glDisableVertexAttribArray(Shader.NORMAL_ATTRIB);
		GL20.glDisableVertexAttribArray(Shader.POSITION_ATTRIB);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		/* un bind shader */
		buffer.clear();
	}

	//
	// END
	//
	
}
