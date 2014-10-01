package engine.nodes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;

import engine.IGameRenderer;
import engine.graphics.PolyModel;
import engine.graphics.Shader;

public class BoxDebugRenderer implements IGameRenderer {

	/* render program */
	private final static Shader SHADER =
			Shader.fromFile("shaders/shadeless.vert", "shaders/shadeless.frag");
	
	/* box model */
	private static PolyModel CUBE = new PolyModel("res/cube.obj");
	
	/* box size */
	public float x,y,z;
	
	public BoxDebugRenderer() {
		this(0, 0, 0);
	}
	
	/**
	 * @param x x size
	 * @param y y size
	 * @param z z size
	 */
	public BoxDebugRenderer(float x, float y, float z) {
		setSize(0,0,0);
	}
	
	/**
	 * @param x x size
	 * @param y y size
	 * @param z z size
	 */
	public void setSize (float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		SHADER.bind();
		float signX = x/Math.abs(x);
		float signY = y/Math.abs(y);
		float signZ = z/Math.abs(z);
		SHADER.uniform3f("offset", 0, 0, 0);
		SHADER.uniform3f("scale", x + signX*0.1f, y*1.05f + signY*0.1f, z*1.05f + signZ*0.1f);
		SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);
		SHADER.uniform4f("tint", 1, 1, 1, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, CUBE.vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, CUBE.totalTris*3);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);	// back to fill or everything will break!
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	//
	// END
	//

}
