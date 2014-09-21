package engine.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.IResourceLoader;
import engine.ResourceManager;

/**
 * @author germangb
 *
 */
public class FrameBuffer implements IResourceLoader {

	private int id;
	private int width;
	private int height;
	private ArrayList<Texture> targets;
	private int targetCount;
	private Texture depthTexture;

	/**
	 * @param context
	 * @param width
	 * @param height
	 */
	public FrameBuffer(int width, int height) {
		this(width, height, 1);
	}

	/**
	 * @param context
	 * @param width
	 * @param height
	 * @param targets
	 */
	public FrameBuffer(int width, int height, int targets) {
		ResourceManager.addResources(this);
		this.width = width;
		this.height = height;
		this.targetCount = targets;
		this.targets = new ArrayList<Texture>();
	}

	//
	// BEGIN IResourceLoader interface implementation
	//
	
	@Override
	public boolean loadResources() {
		/* reset error */
		GL11.glGetError();
		
		id = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);

		// depth
		int depthTex = GL11.glGenTextures();
		this.depthTexture = new Texture(depthTex);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTex, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		// color textures
		for (int i = 0; i < targetCount; ++i) {
			int tex = GL11.glGenTextures();
			this.targets.add(new Texture(tex));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL11.GL_TEXTURE_2D, tex, 0);
		}
		
		// check errors
		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (status != GL30.GL_FRAMEBUFFER_COMPLETE)
			return false;
			//throw new RuntimeException("Frame Buffer Object could not be created. "+status);

		// unbind fbo
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		/* final error check */
		return GL11.glGetError() == GL11.GL_NO_ERROR;
	}
	
	@Override
	public void cleanResources() {
		GL30.glDeleteFramebuffers(id);
	}
	
	//
	// END
	//
	
	/**
	 * encapsulate bind fbo
	 */
	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		IntBuffer bufs = BufferUtils.createIntBuffer(targets.size());
		for (int i = 0; i < targets.size(); ++i)
			bufs.put(GL30.GL_COLOR_ATTACHMENT0 + i);
		bufs.flip();
		GL20.glDrawBuffers(bufs);
	}

	/**
	 * encapsulated fbo unbind
	 */
	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
	}

	/**
	 * Returns a render target
	 * @param target
	 * @return color texture
	 */
	public Texture getColorTexture(int target) {
		if (target >= targets.size())
			throw new RuntimeException("Such render target does not exist");
		return targets.get(target);
	}

	/**
	 * Returns the depth texture of the
	 * frame buffer
	 * @return depth texture
	 */
	public Texture getDepthTexture() {
		return depthTexture;
	}

	/**
	 * @return id of the framebuffer
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return width of the frame buffer
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * returns the height of the frame buffer
	 * @return height of the frame buffer
	 */
	public int getHeight() {
		return height;
	}

}
