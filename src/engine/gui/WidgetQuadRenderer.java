package engine.gui;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;

import engine.IGameRenderer;
import engine.IResourceLoader;
import engine.ResourceManager;
import engine.graphics.Shader;

/**
 * @author germangb
 *
 */
public class WidgetQuadRenderer implements IGameRenderer, IResourceLoader {
	
	/* render program */
	private final static Shader SHADER =
			Shader.fromFile("shaders/shadeless.vert", "shaders/shadeless.frag");
	
	/* rendering stuff */
	private static int vbo;
	private static boolean loaded = false;
	
	private Widget widget;
	private boolean renderMouseDebug;
	
	public WidgetQuadRenderer(Widget w, boolean mouseDebug) {
		this.widget = w;
		this.renderMouseDebug = mouseDebug;
		if (!loaded) {
			loaded = true;
			ResourceManager.addResources(this);
		}
	}
	
	//
	// IGameRenderer interface implementation
	//

	@Override
	public void render(Matrix4f mvp, Matrix4f mv, Matrix4f v) {
		SHADER.bind();
		SHADER.uniform3f("offset", 0, 0, 0);
		SHADER.uniformMat4("modelViewProjectionMatrix", false, mvp);
		float r = 0;
		float g = 0;
		float b = 0;
		if (renderMouseDebug && widget.isHovered()) {
			r = 0;
			g = 1;
			b = 0;
		}
		SHADER.uniform4f("tint", r,g,b, 0.25f);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		SHADER.uniform3f("scale", widget.getSize().width, widget.getSize().height, 1);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		//SHADER.uniform3f("scale", widget.getSize().width, 4, 1);
		//GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	//
	// IResourceLoader interface implementation
	//

	@Override
	public boolean loadResources() {
		vbo = GL15.glGenBuffers();
		FloatBuffer vert = BufferUtils.createFloatBuffer(4*3);
		vert.put(new float[] {
				0,0,0,
				0,1,0,
				1,0,0,
				1,1,0
		});
		vert.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vert, GL15.GL_STATIC_DRAW);
		vert.clear();
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
