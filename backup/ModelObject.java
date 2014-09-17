package game.objects;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import framework.Framework;
import framework.IResourceLoader;
import framework.ResourceManager;
import framework.engine.GameObject;
import framework.engine.IGameRenderer;
import framework.graphics.Mesh;
import framework.graphics.Shader;
import framework.graphics.Texture;
import game.WorldGlobals;

public class ModelObject extends GameObject implements IGameRenderer, IResourceLoader {

	/**
	 * OpenGL shader
	 */
	private final static Shader SHADER = Shader.fromFile("shaders/model.vert", "shaders/model.frag");
	private Texture texture;
	
	/* OpenGL object handlers */
	private int totalTris;
	private int vbo;
	private int nbo;
	private int uvbo;
	
	/**
	 * Creates the game object
	 * @param key
	 */
	public ModelObject(String key) {
		super(key);
		/* add this to the resource loader manager */
		ResourceManager.addResources(this);
		/* add renderer */
		addGameRenderer(this);
		this.texture = Texture.fromFile("res/texture.png").setFilter(GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR);
	}

	//
	// IGameRenderer interface implementation
	//
	
	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		int program = SHADER.getProgram();
		
		/* bind shader */
		GL20.glUseProgram(program);
		
		/* texture binds */
		int refStrLoc = GL20.glGetUniformLocation(program, "texture");
		GL20.glUniform1i(refStrLoc, 0);
		
		/* mvp upload */
		int mvpLoc = GL20.glGetUniformLocation(program, "modelViewProjectionMatrix");
		FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
		mvp.store(mvpBuffer);
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
		
		/* view upload */
		int vLoc = GL20.glGetUniformLocation(program, "viewMatrix");
		FloatBuffer vBuffer = BufferUtils.createFloatBuffer(16);
		v.store(vBuffer);
		vBuffer.flip();
		GL20.glUniformMatrix4(vLoc, false, vBuffer);
		vBuffer.clear();
				
		/* upload time */
		int timeLoc = GL20.glGetUniformLocation(program, "time");
		GL20.glUniform1f(timeLoc, Framework.getInstance().getLocalTime()*0.001f);
		
		/* upload fog start */
		int fogStartLocation = GL20.glGetUniformLocation(program, "fogStart");
		GL20.glUniform1f(fogStartLocation, WorldGlobals.FOG_START);
		
		/* upload fog constant */
		int fogConstLocation = GL20.glGetUniformLocation(program, "fogConst");
		GL20.glUniform1f(fogConstLocation, WorldGlobals.FOG_EXP_CONST);
		
		/* upload fog color */
		int fogColorLocation = GL20.glGetUniformLocation(program, "fogColor");
		int fogColor = WorldGlobals.FOG_COLOR;
		GL20.glUniform3f(fogColorLocation, ((fogColor)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);
		
		/* bind texture */
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
		
		/* draw model */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvbo);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3*totalTris);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		/* un bind shader */
		GL20.glUseProgram(0);
	}
	
	//
	// IResourceLoader interface implementation
	//

	@Override
	public boolean loadResources() {
		Mesh mesh;
		try {
			mesh = new Mesh("res/goal.obj");
		} catch (FileNotFoundException e) {
			return false;
		}
		
		/* create opengl buffers */
		vbo = GL15.glGenBuffers();
		nbo = GL15.glGenBuffers();
		uvbo = GL15.glGenBuffers();
		totalTris = mesh.totalTris;
		
		/* upload data */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mesh.positionData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mesh.normalData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mesh.uvData, GL15.GL_STATIC_DRAW);
		
		/* clean data */
		mesh.clean();
		
		/* return gl error */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(nbo);
		GL15.glDeleteBuffers(uvbo);
	}
	
	//
	//
	//
	
}
