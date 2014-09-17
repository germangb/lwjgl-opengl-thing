package engine.nodes;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import engine.GameNode;
import engine.IGameRenderer;
import engine.WorldGlobals;
import engine.framework.Framework;
import engine.graphics.Shader;
import engine.graphics.VoxelModel;

/**
 * @author germangb
 *
 */
public class VoxelNode extends GameNode implements IGameRenderer {

	private static Shader SHADER = Shader.fromFile("shaders/voxel.vert", "shaders/voxel.frag");
	private VoxelModel model;
	
	/**
	 * @param key
	 */
	public VoxelNode(String key, VoxelModel model) {
		super(key);
		this.model = model;
		addGameRenderer(this);
	}

	//
	// IGameRenderer interface implementation
	//
	
	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		int program = SHADER.getProgram();
		
		/* bind shader */
		SHADER.bind();
		
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
		GL20.glUniform3f(fogColorLocation, ((fogColor)&0xFF)/255.0f, ((fogColor>>8)&0xFF)/255.0f, ((fogColor>>0)&0xFF)/255.0f);
		
		/* upload tint color */
		int tintColorLocation = GL20.glGetUniformLocation(program, "ambientTint");
		int ambientColor = WorldGlobals.AMBIENT_COLOR;
		GL20.glUniform3f(tintColorLocation, ((ambientColor>>16)&0xFF)/255.0f, ((ambientColor>>8)&0xFF)/255.0f, ((ambientColor>>0)&0xFF)/255.0f);
		
		/* draw model */
		int vbo = model.vbo;
		int cbo = model.cbo;
		int totalVoxels = model.totalVoxels;
		GL11.glPointSize(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbo);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glColorPointer(3, GL11.GL_FLOAT, 0, 0);
		GL11.glDrawArrays(GL11.GL_POINTS, 0, totalVoxels);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	//
	// END
	//

}
