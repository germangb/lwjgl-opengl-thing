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
import engine.Time;
import engine.WorldGlobals;
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
	private float phase;
		
	/**
	 * Default constructor
	 */
	public BirdNode() {
		super("bird");
		this.writeShadow = false;
		addGameRenderer(this);
		ResourceManager.addResources(this);
		this.phase = (float) Math.random() * 8;
	}
	
	//
	// IGameRenderer interface implementation

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		int program = SHADER.getProgram();
		SHADER.bind();
		/* upload mvp */
		mvp.store(buffer);
		buffer.flip();
		int mvpLoc = GL20.glGetUniformLocation(program, "modelViewProjectionMatrix");
		GL20.glUniformMatrix4(mvpLoc, false, buffer);
		/* upload time */
		int timeLoc = GL20.glGetUniformLocation(program, "time");
		GL20.glUniform1f(timeLoc, Time.getLocalTime() * 0.001f + phase);
		
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
		
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL20.glEnableVertexAttribArray(Shader.POSITION_ATTRIB);
		GL20.glVertexAttribPointer(Shader.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 0, 0);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 10);
		GL20.glDisableVertexAttribArray(Shader.POSITION_ATTRIB);
		buffer.clear();
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
