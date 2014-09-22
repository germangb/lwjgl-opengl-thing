package engine.nodes;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.GameNode;
import engine.IGameRenderer;
import engine.IResourceLoader;
import engine.ResourceManager;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.graphics.Shader;

/**
 * @author germangb
 *
 */
public class BirdNode extends GameNode implements IGameRenderer, IResourceLoader {
	
	/* shader program */
	private static Shader SHADER = Shader
			.fromFile("shaders/bird.vert", "shaders/bird.frag");
	
	/* vertex buffer */
	private int vbo;
		
	/**
	 * Default constructor
	 */
	public BirdNode() {
		super("bird");
		this.writeShadow = false;
		addGameRenderer(this);
		ResourceManager.addResources(this);
	}
	
	//
	// IGameRenderer interface implementation

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		SHADER.bind();
		/* upload mvp */
		SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);

		int ambientColor = WorldGlobals.AMBIENT_COLOR;
		int fogColor = WorldGlobals.FOG_COLOR;
		SHADER.uniform1f("time", Framework.getInstance().getLocalTime()*0.001f);
		SHADER.uniform1f("fogStart", WorldGlobals.FOG_START);
		SHADER.uniform1f("fogConst", WorldGlobals.FOG_DENSITY);
		SHADER.uniform3f("fogColor", ((fogColor>>16)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);
		SHADER.uniform3f("ambientTint", ((ambientColor>>16)&0xFF)/255.0f, ((ambientColor>>8)&0xFF)/255.0f, ((ambientColor>>0)&0xFF)/255.0f);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL20.glEnableVertexAttribArray(Shader.POSITION_ATTRIB);
		GL20.glVertexAttribPointer(Shader.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 0, 0);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 10);
		GL20.glDisableVertexAttribArray(Shader.POSITION_ATTRIB);
	}
	
	//
	// IResourceLoader interface implementation
	//

	@Override
	public boolean loadResources() {
		vbo = GL15.glGenBuffers();
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(10 * 3);
		vertexData.put(new float[] {
			-2, 0, -0.25f,
			-2, 0, 0.25f,
			-1, 0, -0.5f,
			-1, 0, 0.5f,
			0, 0, -0.5f,
			0, 0, 0.5f,
			1, 0, -0.5f,
			1, 0, 0.5f,
			2, 0, -0.25f,
			2, 0, 0.25f
		});
		vertexData.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		vertexData.clear();
		/* gl check */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}

	@Override
	public void cleanResources() {
		GL15.glDeleteBuffers(vbo);
	}
	
	//
	// END
	//
	
}
